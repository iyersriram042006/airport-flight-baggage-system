package com.airport.bridge;

import com.airport.model.Baggage;

import java.util.List;

// Bridge (Refined Abstraction) + Template Method (concrete class: only varying steps)
public class RegularBaggageProcessor extends BaggageProcessor {

    public RegularBaggageProcessor(BaggageHandlingImplementor implementor) { super(implementor); }

    @Override
    protected void inspect(Baggage baggage, List<String> steps) {
        steps.add("3. Inspect: standard X-ray scan");
    }

    @Override
    protected void determineHandling(Baggage baggage, List<String> steps) {
        steps.add("4. Handling requirement: regular queue");
    }
}
