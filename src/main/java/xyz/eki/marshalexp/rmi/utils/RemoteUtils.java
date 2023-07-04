package xyz.eki.marshalexp.rmi.utils;

import sun.rmi.server.MarshalOutputStream;
import sun.rmi.transport.ObjectTable;
import sun.rmi.transport.Target;
import sun.rmi.transport.TransportConstants;

import javax.net.SocketFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.server.*;
import java.security.*;

public class RemoteUtils {
    public static RemoteRef extractRef(Remote instance) throws IllegalArgumentException, IllegalAccessException
    {
        Field proxyField = null;
        Field remoteField= null;
        RemoteRef remoteRef = null;

        try {
            proxyField = Proxy.class.getDeclaredField("h");
            remoteField = RemoteObject.class.getDeclaredField("ref");
            proxyField.setAccessible(true);
            remoteField.setAccessible(true);

        } catch(NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        if( Proxy.isProxyClass(instance.getClass()) )
            remoteRef = ((RemoteObjectInvocationHandler)proxyField.get(instance)).getRef();

        else
            remoteRef = (RemoteRef)remoteField.get(instance);

        return remoteRef;
    }

    public static String getClassName(Remote remoteObject)
    {
        if( Proxy.isProxyClass(remoteObject.getClass()) ) {

            Class<?>[] interfaces = remoteObject.getClass().getInterfaces();

            for(Class<?> intf : interfaces) {

                String intfName = intf.getName();

                if(!intfName.equals("java.rmi.Remote"))
                    return intfName;
            }
        }

        return remoteObject.getClass().getName();
    }

    private static String getTypeDescriptor(Class<?> var0) {
        if (var0.isPrimitive()) {
            if (var0 == Integer.TYPE) {
                return "I";
            } else if (var0 == Boolean.TYPE) {
                return "Z";
            } else if (var0 == Byte.TYPE) {
                return "B";
            } else if (var0 == Character.TYPE) {
                return "C";
            } else if (var0 == Short.TYPE) {
                return "S";
            } else if (var0 == Long.TYPE) {
                return "J";
            } else if (var0 == Float.TYPE) {
                return "F";
            } else if (var0 == Double.TYPE) {
                return "D";
            } else if (var0 == Void.TYPE) {
                return "V";
            } else {
                throw new Error("unrecognized primitive type: " + var0);
            }
        } else {
            return var0.isArray() ? var0.getName().replace('.', '/') : "L" + var0.getName().replace('.', '/') + ";";
        }
    }

    private static String getMethodNameAndDescriptor(Method var0) {
        StringBuffer var1 = new StringBuffer(var0.getName());
        var1.append('(');
        Class[] var2 = var0.getParameterTypes();

        for(int var3 = 0; var3 < var2.length; ++var3) {
            var1.append(getTypeDescriptor(var2[var3]));
        }

        var1.append(')');
        Class var4 = var0.getReturnType();
        if (var4 == Void.TYPE) {
            var1.append('V');
        } else {
            var1.append(getTypeDescriptor(var4));
        }

        return var1.toString();
    }

    public static long computeMethodHash(Method method) {
        long hash = 0L;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(127);

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA");
            DataOutputStream dataOutputStream = new DataOutputStream(new DigestOutputStream(byteArrayOutputStream, sha1));
            String methodNameAndDescriptor = getMethodNameAndDescriptor(method);

            dataOutputStream.writeUTF(methodNameAndDescriptor);
            dataOutputStream.flush();
            byte[] hashArray = sha1.digest();

            for(int i = 0; i < Math.min(8, hashArray.length); ++i) {
                hash += (long)(hashArray[i] & 255) << i * 8;
            }
        } catch (IOException ignore) {
            hash = -1L;
        } catch (NoSuchAlgorithmException complain) {
            throw new SecurityException(complain.getMessage());
        }

        return hash;
    }

    public static long computeMethodHash(String methodNameAndDescriptor) {
        long hash = 0L;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(127);

        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA");
            DataOutputStream dataOutputStream = new DataOutputStream(new DigestOutputStream(byteArrayOutputStream, sha1));

            dataOutputStream.writeUTF(methodNameAndDescriptor);
            dataOutputStream.flush();
            byte[] hashArray = sha1.digest();

            for(int i = 0; i < Math.min(8, hashArray.length); ++i) {
                hash += (long)(hashArray[i] & 255) << i * 8;
            }
        } catch (IOException ignore) {
            hash = -1L;
        } catch (NoSuchAlgorithmException complain) {
            throw new SecurityException(complain.getMessage());
        }

        return hash;
    }

    /**
     * This code was copied from the following link and is just used to disable the annoying reflection warnings:
     *
     * https://stackoverflow.com/questions/46454995/how-to-hide-warning-illegal-reflective-access-in-java-9-without-jvm-argument
     */
    public static void disableWarning()
    {
        try {
            Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            sun.misc.Unsafe u = (sun.misc.Unsafe) theUnsafe.get(null);

            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {}
    }


    public static void sendRawCall(String host, int port, ObjID objid, int opNum, Long hash, Object ...objects) throws Exception {
        Socket socket = SocketFactory.getDefault().createSocket(host, port);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        DataOutputStream dos = null;
        try {
            OutputStream os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            dos.writeInt(TransportConstants.Magic);
            dos.writeShort(TransportConstants.Version);
            dos.writeByte(TransportConstants.SingleOpProtocol);
            dos.write(TransportConstants.Call);

            final ObjectOutputStream objOut = new myMarshalOutputStream(dos);

            objid.write(objOut); //Objid

            objOut.writeInt(opNum); // opnum
            objOut.writeLong(hash); // hash

            for (Object object:
                    objects) {
                objOut.writeObject(object);
            }
            //objOut.writeObject(object);
            //objOut.writeObject(methodArgs);

            os.flush();

//            ByteArrayOutputStream bout = new ByteArrayOutputStream();
//            InputStream ins = socket.getInputStream();
//            byte [] buf = new byte[1];
//
//            while(ins.read(buf) != -1){
//                bout.write(buf);
//            }
//
//            byte [] returnData = bout.toByteArray();
//            return returnData;
        } finally {
            if (dos != null) {
                dos.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
    }
    public static class myMarshalOutputStream extends ObjectOutputStream{

        public myMarshalOutputStream(OutputStream var1) throws IOException {
            this(var1, 1);
        }

        public myMarshalOutputStream(OutputStream var1, int var2) throws IOException {
            super(var1);
            this.useProtocolVersion(var2);
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    myMarshalOutputStream.this.enableReplaceObject(true);
                    return null;
                }
            });
        }

        protected Object replaceObject(Object var1) throws IOException {
            return var1;
        }

        protected void annotateClass(Class<?> var1) throws IOException {
            this.writeLocation(RMIClassLoader.getClassAnnotation(var1));
        }

        protected void annotateProxyClass(Class<?> var1) throws IOException {
            this.annotateClass(var1);
        }

        protected void writeLocation(String var1) throws IOException {
            this.writeObject(var1);
        }
    }
}
