package com.airport.abstractfactory;

public class InternationalGate implements Gate {
    @Override
    public String describe() { return "International Gate assigned (customs and immigration access)."; }
}
