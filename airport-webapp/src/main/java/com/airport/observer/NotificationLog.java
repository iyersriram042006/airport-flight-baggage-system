package com.airport.observer;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

// In-memory sink the observers (and the mediator's control centre) write into.
// The frontend polls GET /api/notifications to display them.
@Component
public class NotificationLog {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final int MAX_MESSAGES = 60;
    private final LinkedList<String> messages = new LinkedList<>();

    public synchronized void add(String message) {
        messages.addFirst("[" + LocalTime.now().format(TIME_FMT) + "] " + message);
        while (messages.size() > MAX_MESSAGES) messages.removeLast();
    }

    public synchronized List<String> getRecent() {
        return new ArrayList<>(messages);
    }
}
