package com.example.idledetector.service;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.PodMetrics;
import io.fabric8.openshift.client.OpenShiftClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {
    private final OpenShiftClient client;

    public double getPodCpuUsagePercentage(Pod pod){
        try{
            PodMetrics metrics = client.top()
                    .pods()
                    .metrics(pod.getMetadata().getNamespace(), pod.getMetadata().getName());

            if( metrics == null || metrics.getContainers().isEmpty()){
                return 0.0;
            }

            //Sum CPU usage across all containers
            long totalCpuNano = metrics.getContainers().stream()
                    .mapToLong(container ->{
                        String cpu = container.getUsage().get("cpu").getAmount();
                        return parseCpuToNano(cpu);
                    })
                    .sum();
            //Get pod's CPU request/limit
            long cpuRequestNano = pod.getSpec().getContainers().stream()
                    .mapToLong(container ->{
                        if (container.getResources() != null &&
                        container.getResources().getRequests() != null){
                            String request = container.getResources()
                                    .getRequests()
                                    .get("cpu")
                                    .getAmount();
                            return parseCpuToNano(request);
                        }
                        return 10000000L; //Default 1 crore
                    })
                    .sum();
            double percentage = (totalCpuNano * 100.0) / cpuRequestNano;
            return BigDecimal.valueOf(percentage)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        } catch (Exception e){
            log.warn("Failed to get CPU metrics for pod {}: {}",
                    pod.getMetadata().getName(), e.getMessage());
            return 0.0;
        }
    }

    public double getPodMemoryUsagePercentage(Pod pod){
        try{
            PodMetrics metrics = client.top()
                    .pods()
                    .metrics(pod.getMetadata().getNamespace(), pod.getMetadata().getName());

            if(metrics == null || metrics.getContainers().isEmpty()){
                return 0.0;
            }

            //Sum memory usage across all containers
            long totalMemoryBytes = metrics.getContainers().stream()
                    .mapToLong(container -> {
                        String memory = container.getUsage().get("memory").getAmount();
                        return parseMemoryToBytes(memory);
                    })
                    .sum();
            //Get pod's memmory request/limit
            long memoryRequestBytes = pod.getSpec().getContainers().stream()
                    .mapToLong(container ->{
                        if (container.getResources() != null &&
                                container.getResources().getRequests() != null){
                            String request = container.getResources()
                                    .getRequests()
                                    .get("memory")
                                    .getAmount();
                            return parseMemoryToBytes(request);
                        }
                        return 1073741824L;
                    })
                    .sum();
            double percentage = (totalMemoryBytes * 100.0) / memoryRequestBytes;
            return BigDecimal.valueOf(percentage)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        catch (Exception e){
            log.warn("Failed to get memory metrics for pods {}: {}",
                    pod.getMetadata().getName(), e.getMessage());
            return 0.0;
        }
    }


    private long parseCpuToNano(String cpu){
        if (cpu.endsWith("n")){
            return Long.parseLong(cpu.substring(0, cpu.length() -1));
        } else if (cpu.endsWith("m")) {
            return Long.parseLong(cpu.substring(0, cpu.length() -1)) * 10000000L;
        } else {
            return (long) (Double.parseDouble(cpu)*10000000L);
        }
    }

    private long parseMemoryToBytes(String memory){
        if(memory.endsWith("Ki")){
            return Long.parseLong(memory.substring(0, memory.length() -2)) * 1024L;
        } else if (memory.endsWith("Mi")){
            return Long.parseLong(memory.substring(0, memory.length() -2)) * 1024L * 1024L;
        } else if (memory.endsWith("Gi")) {
            return Long.parseLong(memory.substring(0, memory.length() -2)) * 1024L * 1024L * 1024L;
        } else {
            return Long.parseLong(memory);
        }
    }

}
