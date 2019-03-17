package com.bisnode.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Properties;

@Configuration
public class KafkaStreamsConfig {

    @Bean
    public Map<MetricName, ? extends Metric> createStream(){

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        ObjectMapper om = new ObjectMapper();
        StreamsBuilder builder = new StreamsBuilder();

        builder.stream("danish-individuals")
                .foreach((key, value) -> System.out.println("value = " + value));

        builder.build();

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        return streams.metrics();
    }

}
