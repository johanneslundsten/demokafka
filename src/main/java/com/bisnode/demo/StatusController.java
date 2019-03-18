package com.bisnode.demo;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StatusController {


    private final List<KafkaStreams> kafkaStreams;

    public StatusController(List<KafkaStreams> kafkaStreams) {
        this.kafkaStreams = kafkaStreams;
    }


    @GetMapping("kafka/metrics")
    @Produces("application/json")
    public Object getMetrics(){
        return kafkaStreams.stream()
                .collect(Collectors.toMap(Object::toString, KafkaStreams::state));
    }
}
