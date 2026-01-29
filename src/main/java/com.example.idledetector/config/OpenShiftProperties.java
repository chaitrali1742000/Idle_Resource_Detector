package com.example.idledetector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix="openshift")
public class OpenShiftProperties {
    @NotBlank
    private String apiUrl;
    @NotBlank
    private String token;
    @NotBlank
    private String namespace;
    private boolean trustCerts = true;

}
