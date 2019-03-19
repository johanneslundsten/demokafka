package com.bisnode.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

/**
 * Created by johlun
 * on 2019-03-18.
 */
public class IndividualSerde implements Serde<Individual> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<Individual> serializer() {
        return new IndividualSerializer();
    }

    @Override
    public Deserializer<Individual> deserializer() {
        return new IndividualDeserializer();
    }

    public class IndividualDeserializer implements Deserializer<Individual> {

        public IndividualDeserializer() {
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {

        }

        @Override
        public Individual deserialize(String topic, byte[] data) {
            try {
                return new ObjectMapper().readValue(data, Individual.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {

        }
    }

    public class IndividualSerializer implements Serializer<Individual> {

        public IndividualSerializer() {
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {}

        @Override
        public byte[] serialize(String topic, Individual data) {
            try {
                return new ObjectMapper().writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {

        }
    }
}
