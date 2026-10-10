package com.airport.bridge;

import com.airport.model.Baggage;

import java.util.List;

// Bridge (Refined Abstraction) + Template Method (concrete class: only varying steps)
public class PriorityBaggageProcessor extends BaggageProcessor {

    public PriorityBaggageProcessor(BaggageHandlingImplementor implementor) { super(implementor); }

    @Override
    protected void inspect(Baggage baggage, List<String> steps) {
        steps.add("3. Inspect: priority tag verified + fast-track scan");
    }

    @Override
    protected void determineHandling(Baggage baggage, List<String> steps) {
        steps.add("4. Handling requirement: expedited, placed at front of belt queue");
    }
}
