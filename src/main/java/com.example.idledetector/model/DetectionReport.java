package com.example.idledetector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionReport {
    private LocalDateTime scanTime;
    private int totalPodsScanned;
    private int idlePodsFound;
    private List<PodResourceUsage> idlePods;
    private double potentialCostSavings;
}

