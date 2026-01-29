package com.example.idledetector.config;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenShiftClientConfig {

    private final OpenShiftProperties properties;

    @Bean
    public OpenShiftClient openShiftClient() {
        log.info("Initializing OpenShift client for: {}", properties.getApiUrl());

        Config config = new ConfigBuilder()
                .withMasterUrl(properties.getApiUrl())
                .withOauthToken(properties.getToken())
                .withNamespace(properties.getNamespace())
                .withTrustCerts(properties.isTrustCerts())
                .build();

        return new DefaultOpenShiftClient(config);

//        KubernetesClient kubernetesClient =
//                new KubernetesClientBuilder().withConfig(config).build();

        //return kubernetesClient.adapt(OpenShiftClient.class);
    }
}
