package com.bisnode.demo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by johlun
 * on 2019-03-18.
 */
public class HashMapSerde implements Serde<HashMap<String, Integer>> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<HashMap<String, Integer>> serializer() {
        return new HashMapSerializer();
    }

    @Override
    public Deserializer<HashMap<String, Integer>> deserializer() {
        return new HashMapDeserializer();
    }

    public class HashMapDeserializer implements Deserializer<HashMap<String, Integer>> {

        public HashMapDeserializer() {
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {

        }

        @Override
        public HashMap<String, Integer> deserialize(String topic, byte[] data) {
            try {
                return new ObjectMapper().readValue(data, new TypeReference<HashMap<String, Integer>>(){});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {

        }
    }

    public class HashMapSerializer implements Serializer<HashMap<String, Integer>> {

        public HashMapSerializer() {
        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {}

        @Override
        public byte[] serialize(String topic, HashMap<String, Integer> data) {
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
