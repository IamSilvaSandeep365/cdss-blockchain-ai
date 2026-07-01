package com.cdss.backend.controller;

import com.cdss.backend.service.FlaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    private final FlaskService flaskService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("springBoot", "UP");
        status.put("flaskAI",    flaskService.isFlaskHealthy() ? "UP" : "DOWN");
        status.put("timestamp",  new Date().toString());
        return ResponseEntity.ok(status);
    }
}
