package abstractfactory;

// Abstract Factory Pattern - Concrete Factory
public class DomesticAirportFactory implements AirportResourceFactory {
    @Override
    public Gate createGate() {
        return new DomesticGate();
    }

    @Override
    public BaggageService createBaggageService() {
        return new DomesticBaggageService();
    }
}
