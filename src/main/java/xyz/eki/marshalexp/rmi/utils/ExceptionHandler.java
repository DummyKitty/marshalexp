package xyz.eki.marshalexp.rmi.utils;

/**
 * 处理异常
 */
public class ExceptionHandler {
    public static <T extends Throwable> void stackTrace(T e)
    {
        Logger.eprintln("StackTrace:");
        e.printStackTrace();
    }

    public static void exit(){
        Logger.eprintln("Cannot continue from here.");
        System.exit(1);
    }

    public static void internalError(String functionName, String message)
    {
        Logger.eprintlnMixedYellow("Internal error within the", functionName, "function.");
        Logger.eprintln(message);
        exit();
    }

    public static void internalException(Exception e, String functionName, boolean exit)
    {
        Logger.eprintMixedYellow("Internal error. Caught unexpected", e.getClass().getName(), "within the ");
        Logger.printlnPlainMixedBlue(functionName, "function.");
        stackTrace(e);

        if(exit) exit();
    }
}
