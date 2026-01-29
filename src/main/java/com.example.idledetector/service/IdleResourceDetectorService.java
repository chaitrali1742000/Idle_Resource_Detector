package com.example.idledetector.service;


import com.example.idledetector.config.DetectorProperties;
import com.example.idledetector.model.DetectionReport;
import com.example.idledetector.model.PodResourceUsage;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.openshift.client.OpenShiftClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static io.fabric8.kubernetes.client.utils.PodStatusUtil.isRunning;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdleResourceDetectorService {
    private final OpenShiftClient client;
    private final MetricsService metricsService;
    private final DetectorProperties detectorProperties;

    public DetectionReport scanForIdleResources(){
        log.info("Starting idle resource detection scan...");

        List<Pod> allPods = client.pods().inAnyNamespace().list().getItems();
        List<PodResourceUsage> idlePods =  new ArrayList<>();

        for(Pod pod : allPods){
            if(isRunning(pod) && isOldEnough(pod)){
                double cpuUsage = metricsService.getPodCpuUsagePercentage(pod);
                double memoryUsage = metricsService.getPodMemoryUsagePercentage(pod);

                if(cpuUsage < detectorProperties.getCpuThreshold()){
                    PodResourceUsage usage = buildPodResourceUsage(pod, cpuUsage, memoryUsage);
                    idlePods.add(usage);
                    log.info("Idle pods detected: {} - CPU: {}%, Memory: {}%",
                            pod.getMetadata().getName(), cpuUsage, memoryUsage);
                }
            }
        }

        return DetectionReport.builder()
                .scanTime(LocalDateTime.now())
                .totalPodsScanned(allPods.size())
                .idlePodsFound(idlePods.size())
                .idlePods(idlePods)
                .potentialCostSavings(calculateSavings(idlePods))
                .build();
    }

    private boolean isRunning (Pod pod){
        return pod.getStatus() != null &&
                "Running".equals(pod.getStatus().getPhase());
    }

    private boolean isOldEnough(Pod pod){
        if (pod.getStatus() == null || pod.getStatus().getStartTime() == null){
            return false;
        }

        LocalDateTime startTime = LocalDateTime.parse(
                pod.getStatus().getStartTime(),
                DateTimeFormatter.ISO_DATE_TIME
        );
        long daySinceStart = ChronoUnit.DAYS.between(startTime, LocalDateTime.now());
        return daySinceStart >= detectorProperties.getMonitoringDays();
    }

    private PodResourceUsage buildPodResourceUsage(Pod pod, double cpuUsage, double memoryUsage){
        LocalDateTime startTime = LocalDateTime.parse(
                pod.getStatus().getStartTime(),
                DateTimeFormatter.ISO_DATE_TIME
        );

        int daysIdle = (int) ChronoUnit.DAYS.between(startTime, LocalDateTime.now());

        return PodResourceUsage.builder()
                .podName(pod.getMetadata().getName())
                .namespace(pod.getMetadata().getNamespace())
                .avgCpuUsage(cpuUsage)
                .avgMemoryUsage(memoryUsage)
                .firstDetected(startTime)
                .daysIdle(daysIdle)
                .recommendation(generateRecommendation(cpuUsage, memoryUsage))
                .owner(getOwner(pod))
                .build();
    }

    private String generateRecommendation(double cpuUsage, double memoryUsage){
        if(cpuUsage < 1.0 && memoryUsage < 5.0){
            return "REMOVE - Extremely low resource usage";
        } else if (cpuUsage < 3.0) {
            return "DOWNSIZE - Consider reducing CPU allocation by 50%";
        } else {
            return "REVIEW - Monitor for potential downsizing";
        }
    }

    private String getOwner(Pod pod){
        if(pod.getMetadata().getOwnerReferences() != null &&
        !pod.getMetadata().getOwnerReferences().isEmpty()){
            return pod.getMetadata().getOwnerReferences().get(0).getName();
        }
        return "Unknown";
    }

    private double calculateSavings(List<PodResourceUsage> idlePods){
        //Simplified cost calculation - adjust based on cloud provider
        return idlePods.size() * 50.0; // $50 per pod per month estimate
    }

}
