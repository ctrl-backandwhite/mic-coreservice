package com.backandwhite.core.kafka.serializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Deserializador Kafka genérico para mensajes Avro ({@link SpecificRecord}).
 * <p>
 * Resuelve el tipo destino de tres formas (en orden de prioridad):
 * <ol>
 * <li>Clase pasada en el constructor</li>
 * <li>Propiedad {@code avro.target.type} en la configuración del consumer</li>
 * <li>Header {@code avro.type} inyectado por {@link AvroSerializer}</li>
 * </ol>
 */
public class AvroDeserializer<T extends SpecificRecord> implements Deserializer<T> {

    public static final String AVRO_TARGET_TYPE_CONFIG = "avro.target.type";

    private Class<T> targetType;

    /** Constructor sin argumentos — utilizado por Kafka vía reflexión. */
    public AvroDeserializer() {
    }

    /**
     * Constructor con tipo destino explícito — útil en configuraciones
     * programáticas.
     */
    public AvroDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure(Map<String, ?> configs, boolean isKey) {
        if (targetType != null) {
            return;
        }
        Object configuredType = configs.get(AVRO_TARGET_TYPE_CONFIG);
        if (configuredType instanceof Class<?> clazz) {
            targetType = (Class<T>) clazz;
        } else if (configuredType instanceof String className) {
            try {
                targetType = (Class<T>) Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new SerializationException("Could not find Avro target type: " + className, e);
            }
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        return deserialize(topic, (Headers) null, data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(String topic, Headers headers, byte[] data) {
        if (data == null) {
            return null;
        }

        Class<T> resolvedType = resolveType(headers);
        if (resolvedType == null) {
            throw new SerializationException(
                    "Cannot deserialize Avro message: no target type configured, " +
                            "set '" + AVRO_TARGET_TYPE_CONFIG + "' in consumer config or use the " +
                            AvroSerializer.AVRO_TYPE_HEADER + " header.");
        }

        try {
            Schema schema = resolvedType.getDeclaredConstructor().newInstance().getSchema();
            SpecificDatumReader<T> reader = new SpecificDatumReader<>(schema);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (Exception e) {
            throw new SerializationException(
                    "Error deserializing Avro message to " + resolvedType.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<T> resolveType(Headers headers) {
        if (targetType != null) {
            return targetType;
        }
        if (headers != null) {
            Header typeHeader = headers.lastHeader(AvroSerializer.AVRO_TYPE_HEADER);
            if (typeHeader != null) {
                String className = new String(typeHeader.value(), StandardCharsets.UTF_8);
                try {
                    return (Class<T>) Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new SerializationException(
                            "Could not find Avro type from header: " + className, e);
                }
            }
        }
        return null;
    }

    @Override
    public void close() {
        // No-op
    }
}
