package xyz.eki.marshalexp.utils;

import javax.tools.*;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;

public class DynamicCompileUtils {
    public static void main(String[] args) throws Exception{
        String className = "HelloWorld";
        String sourceCode = "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";

        // 创建编译任务
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        JavaFileObject sourceFile = new StringSourceFile(className, sourceCode);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(sourceFile);

        // 设置编译参数和输出位置
        String[] compilerOptions = new String[] {"-d", "bins"};
        StringWriter outputWriter = new StringWriter();

        // 执行编译任务
        compiler.getTask(outputWriter, fileManager, null, Arrays.asList(compilerOptions), null, compilationUnits).call();

        // 输出编译结果
        System.out.println(outputWriter.toString());
    }
}

class StringSourceFile extends SimpleJavaFileObject {
    private final String sourceCode;

    protected StringSourceFile(String className, String sourceCode) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.sourceCode = sourceCode;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceCode;
    }
}
