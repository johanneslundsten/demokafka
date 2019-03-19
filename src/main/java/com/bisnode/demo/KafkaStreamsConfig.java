package com.bisnode.demo;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"Duplicates", "unchecked"})
@Configuration
public class KafkaStreamsConfig {

    private IndividualSerde individualSerde = new IndividualSerde();
    private HashMapSerde hashMapSerde = new HashMapSerde();
    @Value("${kafka.host}")
    private String kafkaHost;


    @Bean
    public CreateTopicsResult createTopics() throws InterruptedException, ExecutionException, TimeoutException {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
        AdminClient adminClient = AdminClient.create(props);

        CreateTopicsResult result = adminClient.createTopics(Arrays.asList(
                new NewTopic("individuals", 1, (short) 1),
                new NewTopic("individuals-2", 1, (short) 1),
                new NewTopic("se-individuals", 1, (short) 1),
                new NewTopic("dk-individuals", 1, (short) 1),
                new NewTopic("se-individuals", 1, (short) 1),
                new NewTopic("se-dk-individuals", 1, (short) 1),
                new NewTopic("individuals-2", 1, (short) 1)
        ));

        result.all().get(10, TimeUnit.SECONDS);
        return result;
    }

    @Bean
    public StatusPojo createSimpleStream(CreateTopicsResult result){
        String name = "simple-stream";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        kafkaHost = kafkaHost;
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);

        StreamsBuilder builder = new StreamsBuilder();

        builder.stream("individuals", Consumed.with(Serdes.String(), individualSerde))
//                .peek((key, value) -> System.out.println("value = " + value))
                .to("individuals-2", Produced.with(Serdes.String(), individualSerde));

        builder.build();

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        return new StatusPojo(name, streams);
    }

    @Bean
    public StatusPojo createBranchStream(CreateTopicsResult result){
        String name = "branch-stream";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, individualSerde.getClass().getName());
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Individual>[] individuals = builder.stream("individuals-2", Consumed.with(Serdes.String(), individualSerde))
                .branch(
                        (gedi, individual) -> "SE".equals(individual.getCountryCode()),
                        (gedi, individual) -> "DK".equals(individual.getCountryCode()),
                        (gedi, individual) -> "NO".equals(individual.getCountryCode()),
                        (gedi, individual) -> true
                );

        individuals[0]
//                .peek((key, value) -> System.out.println("SE " + value))
                .to("se-individuals");

        individuals[1]
//                .peek((key, value) -> System.out.println("DK " + value))
                .to("dk-individuals");

        individuals[2]
//                .peek((key, value) -> System.out.println("NO " + value))
                .to("no-individuals");

        individuals[3]
                .foreach((key, value) -> System.out.println("ALIEN " + value));

        builder.build();
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        return new StatusPojo(name, streams);
    }

    @Bean
    public StatusPojo createMergeStream(CreateTopicsResult result){
        String name = "merge-stream";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, individualSerde.getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        builder.stream("se-individuals", Consumed.with(Serdes.String(), individualSerde))
                .merge(builder.stream("dk-individuals", Consumed.with(Serdes.String(), individualSerde)))
//                .peek((key, value) -> System.out.println("MERGE: " + value))
                .to("se-dk-individuals", Produced.with(Serdes.String(), individualSerde));


        builder.build();

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        return new StatusPojo(name, streams);
    }

    @Bean
    public StatusPojo createJoinStream(CreateTopicsResult result){

        Properties props = new Properties();
        String name = "join-stream";
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, individualSerde.getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Individual> seIndividuals = builder.stream("se-individuals", Consumed.with(Serdes.String(), individualSerde));
        KStream<String, Individual> seDkIndividuals = builder.stream("se-dk-individuals", Consumed.with(Serdes.String(), individualSerde));
        ValueJoiner<Individual, Individual, List<Individual>> vj = (value1, value2) -> Arrays.asList(value1, value2);

        seDkIndividuals
                .join(seIndividuals, vj, JoinWindows.of(1000))
                .foreach((key, value) -> System.out.println("JOIN: " + value));

        builder.build();

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        return new StatusPojo(name, streams);
    }

    @Bean
    public StatusPojo createAggregateStream(CreateTopicsResult result){

        Properties props = new Properties();
        String name = "aggregate-stream";
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, name);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, hashMapSerde.getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        builder.stream("individuals-2", Consumed.with(Serdes.String(), individualSerde))
                .peek((key, value) -> System.out.println("Agg = " + value))
                .groupBy((key, value) -> value.getGender(), Serialized.with(Serdes.String(), individualSerde))
                .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(150)))
                .aggregate(() -> {
                    Map<String, Integer> map = new HashMap<>();
                    map.put("SE", 0);
                    map.put("DK", 0);
                    map.put("NO", 0);
                    return map;
                }, (gender, individual, aggregate) -> {
                    aggregate.computeIfPresent(individual.getCountryCode(), (s, integer) -> integer + individual.getAge());
                    return aggregate;
                })
                .toStream()
                .foreach((key, value) -> System.out.println("WINDOW AGES: " + value));


        builder.build();

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        return new StatusPojo(name, streams);
    }

}
