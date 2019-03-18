package com.bisnode.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@SuppressWarnings({"Duplicates", "unchecked"})
@Configuration
public class KafkaStreamsConfig {

    @Bean
    public KafkaStreams createSimpleStream(){

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "simple-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        Serde<Individual> individualSerde = Serdes.serdeFrom(new IndividualSerializer(), new IndividualDeserializer());

        StreamsBuilder builder = new StreamsBuilder();

        builder.stream("individuals", Consumed.with(Serdes.String(), individualSerde))
                .foreach((key, value) -> System.out.println("value = " + value));

        builder.build();

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        return streams;
    }

//    @Bean
//    public KafkaStreams createBranchStream(){
//
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "branch-stream");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        Serde<Individual> individualSerde = Serdes.serdeFrom(new IndividualSerializer(), new IndividualDeserializer());
//        StreamsBuilder builder = new StreamsBuilder();
//
//        KStream<String, Individual>[] individuals = builder.stream("individuals", Consumed.with(Serdes.String(), individualSerde))
//                .branch(
//                        (gedi, individual) -> "SE".equals(individual.getCountryCode()),
//                        (gedi, individual) -> "DK".equals(individual.getCountryCode()),
//                        (gedi, individual) -> "NO".equals(individual.getCountryCode())
//                );
//
//        individuals[0]
//                .peek((key, value) -> System.out.println("SE " + value))
//                .to("se-individuals");
//
//        individuals[1]
//                .peek((key, value) -> System.out.println("DK " + value))
//                .to("dk-individuals");
//
//        individuals[2]
//                .peek((key, value) -> System.out.println("DK " + value))
//                .to("dk-individuals");
//
//        builder.build();
//        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
//        streams.start();
//        return streams;
//    }
//
//    @Bean
//    public KafkaStreams createMergeStream(){
//
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "branch-stream");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.serdeFrom(new Individual(), new Individual()));
//
//        StreamsBuilder builder = new StreamsBuilder();
//
//        builder.stream("danish-individuals")
//                .foreach((key, value) -> System.out.println("value = " + value));
//
//        builder.build();
//
//        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
//        streams.start();
//        return streams;
//    }

}
