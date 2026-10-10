package com.airport.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Small helper for the "expected baggage slot" feature.
// Deliberately simple: slot = landing time + 15 min  to  landing time + 30 min.
public final class SlotCalculator {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private SlotCalculator() { }

    private static LocalTime parse(String time) {
        return LocalTime.parse(time.trim().toUpperCase().replace(".", ""), FMT);
    }

    public static boolean isValidTime(String time) {
        if (time == null || time.isBlank()) return false;
        try { parse(time); return true; } catch (Exception e) { return false; }
    }

    public static String computeSlot(String landingTime) {
        LocalTime landing = parse(landingTime);
        return landing.plusMinutes(15).format(FMT) + " - " + landing.plusMinutes(30).format(FMT);
    }
}
