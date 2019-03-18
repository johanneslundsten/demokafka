package com.bisnode.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

/**
 * Created by johlun
 * on 2019-03-18.
 */
public class IndividualDeserializer implements Deserializer<Individual> {

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
