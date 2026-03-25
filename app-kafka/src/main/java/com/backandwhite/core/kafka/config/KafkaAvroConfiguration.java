package com.backandwhite.core.kafka.config;

import com.backandwhite.core.kafka.serializer.AvroDeserializer;
import com.backandwhite.core.kafka.serializer.AvroSerializer;

import lombok.extern.log4j.Log4j2;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de Kafka con serialización/deserialización Avro.
 * <p>
 * Los beans marcados con {@link Primary} tienen prioridad sobre los beans
 * String/String definidos en {@code KafkaConfiguration} (app-common).
 * Se activa solo cuando {@code spring.kafka.enabled=true}.
 */
@Log4j2
@EnableKafka
@Configuration
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
public class KafkaAvroConfiguration {

    private static final long RETRY_INTERVAL_MS = 5_000L;
    private static final long MAX_RETRIES = 3L;

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks:all}")
    private String producerAcks;

    @Value("${spring.kafka.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    // ========================= Producer =========================

    @Bean
    @Primary
    public ProducerFactory<String, SpecificRecord> avroProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, producerAcks);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    @Primary
    public KafkaTemplate<String, SpecificRecord> avroKafkaTemplate() {
        return new KafkaTemplate<>(avroProducerFactory());
    }

    // ========================= Consumer =========================

    @Bean
    @Primary
    public ConsumerFactory<String, SpecificRecord> avroConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        // ErrorHandlingDeserializer wraps the real deserializers so that
        // SerializationException is converted to a DeserializationException
        // that DefaultErrorHandler can process (skip poison pills).
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, AvroDeserializer.class);

        log.info("::> Kafka consumer factory configured — bootstrap: {}, auto-offset-reset: {}", bootstrapServers,
                autoOffsetReset);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean("avroKafkaListenerContainerFactory")
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> avroKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SpecificRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(avroConsumerFactory());

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, exception) -> log.error(
                        "::> [DLQ] Message exhausted after {} retries. Topic: {}, Partition: {}, Offset: {}, Error: {}",
                        MAX_RETRIES, record.topic(), record.partition(), record.offset(),
                        exception.getMessage(), exception),
                new FixedBackOff(RETRY_INTERVAL_MS, MAX_RETRIES));

        // Skip poison pill messages that can never be deserialized
        errorHandler.addNotRetryableExceptions(
                org.springframework.kafka.support.serializer.DeserializationException.class);

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
