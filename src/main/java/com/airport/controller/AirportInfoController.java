package com.airport.controller;

import com.airport.model.Baggage;
import com.airport.service.AirportService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AirportInfoController {

    private final AirportService service;

    public AirportInfoController(AirportService service) { this.service = service; }

    @GetMapping("/belts")
    public Map<String, String> belts() { return service.getBelts(); }

    @GetMapping("/baggage")
    public Collection<Baggage> baggage() { return service.getAllBaggage(); }

    // Observer output (passenger / display board / staff / control centre messages).
    @GetMapping("/notifications")
    public List<String> notifications() { return service.getNotifications(); }
}
