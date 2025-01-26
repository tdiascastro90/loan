package com.creditas.loan.broker;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@AllArgsConstructor
public abstract class Kafka {

    private final String bootstrapServers;
    private final ExecutorService executorService;

    protected Kafka(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
        this.executorService = Executors.newCachedThreadPool();
    }

    protected KafkaProducer<String, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    protected KafkaConsumer<String, String> createConsumer(String groupId, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }

    public void sendMessage(String topic, String key, Object value) {
        KafkaProducer<String, String> producer = createProducer();
        try {
            String message = serializeValue(value);
            producer.send(new ProducerRecord<>(topic, key, message), (metadata, exception) -> {
                if (exception != null) {
                    handleError(exception);
                } else {
                    handleSuccess(metadata.topic(), metadata.offset());
                }
            });
        } finally {
            producer.close();
        }
    }

    public void consumeMessages(String groupId, String topic, Consumer<String> messageProcessor) {
        executorService.submit(() -> {
            KafkaConsumer<String, String> consumer = createConsumer(groupId, topic);
            try {
                while (true) {
                    consumer.poll(Duration.ofMillis(100)).forEach(record -> {
                        try {
                            messageProcessor.accept(record.value());
                        } catch (Exception e) {
                            handleError(e);
                        }
                    });
                }
            } finally {
                consumer.close();
            }
        });
    }

    protected abstract String serializeValue(Object value);

    protected abstract void handleSuccess(String topic, long offset);

    protected abstract void handleError(Throwable exception);

    public void shutdown() {
        executorService.shutdown();
    }
}
