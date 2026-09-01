package abstractfactory;

public class InternationalBaggageService implements BaggageService {
    @Override
    public void showServiceInfo() {
        System.out.println("  International Baggage Service ready (customs screening included).");
    }
}
