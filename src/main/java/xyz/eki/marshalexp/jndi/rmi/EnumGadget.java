package xyz.eki.marshalexp.jndi.rmi;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class EnumGadget {
    public static void main(String[] args) {
        // 获取所有已加载的类
        Class<?>[] allLoadedClasses = getAllLoadedClasses();

        // 遍历每个类并检查条件
        for (Class<?> clazz : allLoadedClasses) {
            // 检查类是否有 public 无参构造方法
            Constructor<?>[] constructors = clazz.getConstructors();
            boolean hasPublicNoArgConstructor = false;
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0 && Modifier.isPublic(constructor.getModifiers())) {
                    hasPublicNoArgConstructor = true;
                    break;
                }
            }

            // 检查类是否有 public 只有一个 String 参数的方法
            Method[] methods = clazz.getMethods();
            boolean hasPublicMethodWithStringArg = false;
            for (Method method : methods) {
                if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == String.class
                        && Modifier.isPublic(method.getModifiers())) {
                    hasPublicMethodWithStringArg = true;
                    break;
                }
            }

            // 输出满足条件的类名
            if (hasPublicNoArgConstructor && hasPublicMethodWithStringArg) {
                System.out.println(clazz.getName());
            }
        }
    }

    private static Class<?>[] getAllLoadedClasses() {
        Instrumentation instrumentation = getInstrumentation();
        if (instrumentation != null) {
            return instrumentation.getAllLoadedClasses();
        } else {
            return new Class<?>[0];
        }
    }

    private static Instrumentation getInstrumentation() {
        try {
            // 通过反射获取私有的 'theInstrumentation' 字段
            Class<?> vmClass = Class.forName("sun.misc.VM");
            Field instrumentationField = vmClass.getDeclaredField("theInstrumentation");
            instrumentationField.setAccessible(true);

            // 获取 'theInstrumentation' 字段的值
            return (Instrumentation) instrumentationField.get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
