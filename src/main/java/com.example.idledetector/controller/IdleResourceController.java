package com.example.idledetector.controller;

import com.example.idledetector.model.DetectionReport;
import com.example.idledetector.service.IdleResourceDetectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/idle-resources")
@RequiredArgsConstructor
public class IdleResourceController {
    private final IdleResourceDetectorService detectorService;

    @GetMapping("/scan")
    public ResponseEntity<DetectionReport> triggerScan(){
        DetectionReport report = detectorService.scanForIdleResources();
        return ResponseEntity.ok(report);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Idle Resource Detector is running");
    }


}
