package com.bisnode.demo.domain;

import org.apache.kafka.streams.KafkaStreams;

/**
 * Created by johlun
 * on 2019-03-19.
 */
public class StreamWrapper {
    private final String name;
    private final KafkaStreams kafkaStreams;

    public StreamWrapper(String name, KafkaStreams kafkaStreams) {
        this.name = name;
        this.kafkaStreams = kafkaStreams;
    }

    public String getName() {
        return name;
    }

    public KafkaStreams getKafkaStreams() {
        return kafkaStreams;
    }
}
