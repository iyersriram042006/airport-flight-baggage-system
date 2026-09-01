package abstractfactory;

public class InternationalGate implements Gate {
    @Override
    public void showGateInfo() {
        System.out.println("  International Gate assigned (with customs/immigration access).");
    }
}
