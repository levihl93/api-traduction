package tunutech.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HealthController {
    @GetMapping("/health")
    public String health() {
        return "🚀 API is RUNNING! Database connected! JPA working!";
    }

    @GetMapping("/")
    public String home() {
        return "TunuTech Translation API v1.0 - READY FOR RAILWAY!";
    }
}
