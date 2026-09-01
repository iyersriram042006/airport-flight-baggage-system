package abstractfactory;

public class DomesticGate implements Gate {
    @Override
    public void showGateInfo() {
        System.out.println("  Domestic Gate assigned (local terminal, no customs).");
    }
}
