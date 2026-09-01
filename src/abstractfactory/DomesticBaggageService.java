package abstractfactory;

public class DomesticBaggageService implements BaggageService {
    @Override
    public void showServiceInfo() {
        System.out.println("  Domestic Baggage Service ready (standard handling).");
    }
}
