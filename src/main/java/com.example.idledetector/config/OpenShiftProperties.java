package com.example.idledetector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Data
@Configuration
@ConfigurationProperties(prefix = "openshift")
public class OpenShiftProperties {

    private String apiUrl = "";
    private String token = "";
    private String namespace = "default";
    private boolean trustCerts = true;
}

