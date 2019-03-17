package com.bisnode.demo;

import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StatusController {


    private final Map<MetricName, ? extends Metric> metadata;

    public StatusController(Map<MetricName, ? extends Metric> metadata) {
        this.metadata = metadata;
    }


    @GetMapping("kafka/metrics")
    @Produces("application/json")
    public Object getMetrics(){
        return metadata.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().metricValue()));
    }
}
