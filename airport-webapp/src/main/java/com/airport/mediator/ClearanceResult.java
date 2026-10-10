package com.airport.mediator;

import java.util.List;

public record ClearanceResult(boolean approved, List<String> steps) { }
