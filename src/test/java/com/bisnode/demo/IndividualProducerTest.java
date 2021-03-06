package com.bisnode.demo;

import com.bisnode.demo.domain.Individual;
import com.bisnode.demo.domain.IndividualSerde;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

@Ignore
public class IndividualProducerTest {
    @Test
    public void produce(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        KafkaProducer<String, Individual> producer = new KafkaProducer<>(props, new StringSerializer(), new IndividualSerde().serializer());
        try {
            while (true) {
                Individual individual = Individual.buildARandom();
                producer.send(new ProducerRecord<>("individuals", individual.getGedi(), individual));
                Thread.sleep(1000);
            }
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            producer.close();
        } catch (KafkaException e) {
            // For all other exceptions, just abort the transaction and try again.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        producer.close();
    }
}
