package com.example.bpm;

import com.example.bpm.model.HubEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;

public class KafkaProducerApp {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            ObjectMapper mapper = new ObjectMapper();
            
            String source = "opus"; // default
            String pnr = null;
            
            if (args.length > 0) {
                source = args[0];
            }
            
            if (args.length > 1) {
                pnr = args[1];
            }

            HubEvent event = new HubEvent(
                    "hubevent",
                    "agent-007",
                    "375987654321001",
                    source,
                    pnr,
                    Instant.now().toString()
            );

            String json = mapper.writeValueAsString(event);
            producer.send(new ProducerRecord<>("hub-events", json));
            
            System.out.println("Sent event: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
