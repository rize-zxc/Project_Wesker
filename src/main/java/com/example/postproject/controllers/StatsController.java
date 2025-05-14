package com.example.postproject.controllers;

import com.example.postproject.services.RequestCounter;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final RequestCounter requestCounter;

    public StatsController(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @GetMapping("/requests")
    public Map<String, Integer> getRequestCount() {
        return Map.of("totalRequests", requestCounter.getCount());
    }

    @PostMapping("/requests/reset")
    public void resetCounter() {
        requestCounter.reset();
    }
}