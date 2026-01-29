package com.example.idledetector.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "detector")
public class DetectorProperties{
    private double cpuThreshold = 5.0;

    private double memoryThreshold = 10.0;

    private int monitoringDays = 7;

    private String scancron = "0 0 2 * * ?";

    private boolean enabled = true;
}