package com.example.idledetector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PodResourceUsage {
    private String podName;
    private String namespace;
    private double avgCpuUsage;
    private double avgMemoryUsage;
    private LocalDateTime firstDetected;
    private int daysIdle;
    private String recommendation;
    private String owner;
}
