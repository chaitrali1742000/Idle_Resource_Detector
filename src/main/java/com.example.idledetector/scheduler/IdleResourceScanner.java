package com.example.idledetector.scheduler;

import com.example.idledetector.service.IdleResourceDetectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdleResourceScanner {
    private final IdleResourceDetectorService detectorService;

    // This uses the cron expression from your application.yml
    @Scheduled(cron = "${detector.scan-cron}")
    public void runScheduledScan() {
        log.info("Executing scheduled idle resource scan...");
        detectorService.scanForIdleResources();
        // In a real app, you'd probably email the report here
    }
}