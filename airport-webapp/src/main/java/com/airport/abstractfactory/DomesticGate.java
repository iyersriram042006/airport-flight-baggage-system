package com.airport.abstractfactory;

public class DomesticGate implements Gate {
    @Override
    public String describe() { return "Domestic Gate assigned (local terminal, no customs)."; }
}
