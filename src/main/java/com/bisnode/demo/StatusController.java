package com.bisnode.demo;

import com.bisnode.demo.domain.StatusPojo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StatusController {


    private final List<StatusPojo> kafkaStreams;

    public StatusController(List<StatusPojo> kafkaStreams) {
        this.kafkaStreams = kafkaStreams;
    }


    @GetMapping("kafka/status")
    @Produces("application/json")
    public Object getMetrics(){
        return kafkaStreams.stream()
                .collect(Collectors.toMap(StatusPojo::getName, statusPojo -> statusPojo.getKafkaStreams().state()));
    }
}
