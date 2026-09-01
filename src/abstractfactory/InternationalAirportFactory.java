package abstractfactory;

// Abstract Factory Pattern - Concrete Factory
public class InternationalAirportFactory implements AirportResourceFactory {
    @Override
    public Gate createGate() {
        return new InternationalGate();
    }

    @Override
    public BaggageService createBaggageService() {
        return new InternationalBaggageService();
    }
}
