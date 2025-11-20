package com.example.bpm;

import com.example.bpm.model.HubEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerApp {
    public static void main(String[] args) {
        // Temporal Client
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        ObjectMapper mapper = new ObjectMapper();

        // Kafka Consumer Config
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "bpm-poc-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Redis Client
        try (redis.clients.jedis.JedisPool jedisPool = new redis.clients.jedis.JedisPool("localhost", 6379)) {
            
            try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
                consumer.subscribe(Collections.singletonList("hub-events"));
                System.out.println("Kafka consumer started...");

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        try {
                            System.out.println("Received message: " + record.value());
                            HubEvent event = mapper.readValue(record.value(), HubEvent.class);
                            
                            String agentid = event.getAgentid();
                            String cardMemberNumber = event.getCardMemberNumber();
                            String source = event.getSource();
                            
                            if (agentid == null || cardMemberNumber == null || source == null) {
                                System.err.println("Invalid event: missing required fields");
                                continue;
                            }

                            String redisKey = "session:" + agentid + ":" + cardMemberNumber;

                            if ("opus".equalsIgnoreCase(source)) {
                                try (redis.clients.jedis.Jedis jedis = jedisPool.getResource()) {
                                    jedis.set(redisKey, "ACTIVE");
                                    jedis.expire(redisKey, 3600); // 1 hour TTL
                                    System.out.println("Session created/updated in Redis: " + redisKey);
                                }
                            } else if ("sabre".equalsIgnoreCase(source)) {
                                boolean sessionExists = false;
                                try (redis.clients.jedis.Jedis jedis = jedisPool.getResource()) {
                                    sessionExists = jedis.exists(redisKey);
                                }

                                if (sessionExists) {
                                    System.out.println("Session exists in Redis. Starting/Signaling workflow.");
                                    String workflowId = "session-" + agentid + "-" + cardMemberNumber;

                                    WorkflowOptions options = WorkflowOptions.newBuilder()
                                            .setTaskQueue("hub-events-queue")
                                            .setWorkflowId(workflowId)
                                            .build();

                                    SessionWorkflow workflow = client.newWorkflowStub(SessionWorkflow.class, options);

                                    try {
                                        io.temporal.client.BatchRequest request = client.newSignalWithStartRequest();
                                        request.add(workflow::run, event);
                                        request.add(workflow::hubEventSignal, event);
                                        client.signalWithStart(request);
                                        
                                        System.out.println("Signaled/Started workflow " + workflowId);
                                    } catch (Exception e) {
                                         System.err.println("Error processing workflow for " + workflowId + ": " + e.getMessage());
                                         e.printStackTrace();
                                    }
                                } else {
                                    // ERROR CASE: Sabre event without OPUS session
                                    System.err.println("ERROR: Sabre event received but no active session found for " + redisKey);
                                    System.err.println("ACTION REQUIRED: Please load Card Member profile in OPUS for better experience.");
                                    // In a real app, we would publish this error to an error topic or send a notification
                                }
                            } else {
                                System.out.println("Ignored event source: " + source);
                            }

                        } catch (Exception e) {
                            System.err.println("Error processing message: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
