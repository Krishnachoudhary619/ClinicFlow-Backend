package com.clinicflow.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, String> welcome() {
        return Map.of(
                "message", "Welcome to ClinicFlow API",
                "version", "1.0.0",
                "status", "Running");
    }
}
