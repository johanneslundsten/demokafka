package com.bisnode.demo;

import com.bisnode.demo.domain.HashMapSerde;
import com.bisnode.demo.domain.Individual;
import com.bisnode.demo.domain.IndividualSerde;
import com.bisnode.demo.domain.StreamWrapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by johlun
 * on 2019-03-19.
 */
@SuppressWarnings({"unchecked", "Duplicates"})
@Configuration
public class KafkaStreamsConfig {

    private IndividualSerde individualSerde = new IndividualSerde();;

    //Simple stream, map
    @Bean
    public StreamWrapper createSimpleStream(){

        String name = "simple-stream";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        StreamsBuilder builder = new StreamsBuilder();

        builder.stream("individuals", Consumed.with(Serdes.String(), individualSerde))
                .peek((key, value) -> System.out.println("INPUT = " + value))
                .map((key, value) -> KeyValue.pair(key, value.setAge(value.getAge() + 1)))
                .to("individuals-2", Produced.with(Serdes.String(), individualSerde));

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();

        return new StreamWrapper(name, streams);
    }

    //Branch a stream
    @Bean
    public StreamWrapper publicBranchStream(){

        String name = "branch-stream";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Individual>[] kStreams = builder.stream("individuals-2", Consumed.with(Serdes.String(), individualSerde))
                .branch(
                        (key, value) -> "SE".equals(value.getCountryCode()),
                        (key, value) -> "DK".equals(value.getCountryCode()),
                        (key, value) -> "NO".equals(value.getCountryCode()),
                        (key, value) -> true
                        );

        kStreams[0]
                .peek((key, value) -> System.out.println("SE = " + value))
                .to("se-individuals", Produced.with(Serdes.String(), individualSerde));

        kStreams[1]
                .peek((key, value) -> System.out.println("DK = " + value))
                .to("dk-individuals", Produced.with(Serdes.String(), individualSerde));

        kStreams[2]
                .peek((key, value) -> System.out.println("NO = " + value))
                .to("no-individuals", Produced.with(Serdes.String(), individualSerde));

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();

        return new StreamWrapper(name, streams);
    }

    //Join two streams

    public StreamWrapper createJoin(){
        String name = "join-stream";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, individualSerde.getClass().getName());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Individual> individuals = builder.stream("individuals", Consumed.with(Serdes.String(), individualSerde));
        KStream<String, Individual> seIndividuals = builder.stream("se-individuals", Consumed.with(Serdes.String(), individualSerde));
        ValueJoiner<Individual, Individual, List<Individual>> valueJoiner = (value1, value2) -> Arrays.asList(value1, value2);

        individuals.join(seIndividuals, valueJoiner, JoinWindows.of(2000))
                    .foreach((key, value) -> System.out.println("JOIN = " + value));

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();

        return new StreamWrapper(name, streams);
    }

    //Aggregation

    @Bean
    public StreamWrapper createAgg(){
        String name = "agg-stream";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, HashMapSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        builder.stream("individuals", Consumed.with(Serdes.String(), individualSerde))
                .groupBy((key, value) -> value.getGender(), Serialized.with(Serdes.String(), individualSerde))
                .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(2)))
                .aggregate(
                        () -> {
                            HashMap<String, Integer> map = new HashMap<>();
                            map.put("SE", 0);
                            map.put("DK", 0);
                            map.put("NO", 0);
                            return map;
                        },(gender, individual, map) -> {
                            map.computeIfPresent(individual.getCountryCode(), (s, integer) -> integer + individual.getAge());
                            return map;
                        }
                ).toStream().foreach((key, value) -> System.out.println("SUMAGE = " + value));



        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();

        return new StreamWrapper(name, streams);
    }


}
