package xyz.eki.marshalexp.rmi.utils;


import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.rmi.Remote;
import java.rmi.server.RemoteStub;
import java.util.HashSet;
import java.util.Set;

public class ReflectUtils {

    private static ClassPool pool;
    private static CtClass remoteClass;
    private static CtClass remoteStubClass;
    private static Set<String> createdClasses;

    /**
     * 初始化存储remoteClass和remoteStubClass方便生成接口时调用
     */
    static {
        pool = ClassPool.getDefault();

        try {
            remoteClass = pool.getCtClass(Remote.class.getName());
            remoteStubClass = pool.getCtClass(RemoteStub.class.getName());
        } catch (NotFoundException e) {
            ExceptionHandler.internalError("ReflectUtil.init", "Caught unexpected NotFoundException.");
        }

        createdClasses = new HashSet<String>();
    }

    /**
     * 将RMIClassLoader设置我们自定义的RMICLASSLoader
     */
    public static void enableCustomRMIClassLoader()
    {
        System.setProperty("java.rmi.server.RMIClassLoaderSpi", "xyz.eki.marshalexp.rmi.utils.CustomRMIClassLoader");
    }

    /**
     * 生成对应的远程接口，继承自Remote
     *
     * @param className 类名
     * @return created 生成类
     * @throws CannotCompileException 编译错误
     */
    public static Class makeInterface(String className) throws CannotCompileException
    {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {}

        CtClass intfClz = pool.makeInterface(className, remoteClass);
        createdClasses.add(className);

        return intfClz.toClass();
    }

    /**
     * 设置类serialVersionUID字段为2L,对于一些远程类有用
     *
     * @param ctClass class where the serialVersionUID should be added to
     * @throws CannotCompileException should never be thrown in practice
     */
    private static void addSerialVersionUID(CtClass ctClass) throws CannotCompileException
    {
        CtField serialID = new CtField(CtPrimitiveType.longType, "serialVersionUID", ctClass);
        serialID.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
        ctClass.addField(serialID, CtField.Initializer.constant(2L));
    }

    /**
     * 这个函数与makeInterface类似，但是作用于传统的RMI Remote Stub机制
     * 其中生成的临时接口类需要设置serialVersionUID为2来满足RMI RemoteStub的默认值
     */
    public static Class makeLegacyStub(String className) throws CannotCompileException, NotFoundException
    {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {}

        makeInterface(className + "Interface");
        CtClass intf = pool.getCtClass(className + "Interface");

        CtClass ctClass = pool.makeClass(className, remoteStubClass);
        ctClass.setInterfaces(new CtClass[] { intf });
        addSerialVersionUID(ctClass);

        createdClasses.add(className);
        return ctClass.toClass();
    }



    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static Object getFieldValve(Object obj, final String fieldName) {
        Field field = null;
        Class<?> clazz = obj.getClass();
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static void setFieldValue(final Object obj, final String fieldName, final Object value) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    public static byte[] WriteObjectToBytes(Object obj) throws Exception{
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(obj);
        oos.close();
        return barr.toByteArray();
    }

    public static void readObjectFromBytes(byte[] objBytes) throws  Exception{
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(objBytes));
        Object o = (Object) ois.readObject();
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
