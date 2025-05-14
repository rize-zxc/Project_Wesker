package com.example.postproject.services;

import com.example.postproject.services.RequestCounter;
import com.example.postproject.models.ServerStatus;
import com.example.postproject.singleton.ServerStatusSingleton;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatusService {
    private final ServerStatus serverStatus;
    private final RequestCounter requestCounter;

    public StatusService(RequestCounter requestCounter) {
        this.serverStatus = ServerStatusSingleton.getInstance();
        this.requestCounter = requestCounter;
    }

    public boolean isServerAvailable() {
        requestCounter.increment();
        return serverStatus.isAvailable();
    }

    public Map<String, String> updateAndGetStatus(String status) {
        requestCounter.increment();

        if (status != null) {
            if ("available".equalsIgnoreCase(status)) {
                serverStatus.setAvailable(true);
            } else if ("unavailable".equalsIgnoreCase(status)) {
                serverStatus.setAvailable(false);
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", serverStatus.isAvailable() ? "available" : "unavailable");
        response.put("message", serverStatus.isAvailable()
                ? "Сервис работает в штатном режиме"
                : "Сервис временно недоступен");
        response.put("totalRequests", String.valueOf(requestCounter.getCount()));

        return response;
    }
}
