package com.backandwhite.core.kafka.serializer;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Serializador Kafka genérico para mensajes Avro ({@link SpecificRecord}).
 * <p>
 * Añade automáticamente un header {@code avro.type} con el nombre completo
 * de la clase, permitiendo al {@link AvroDeserializer} resolver el tipo
 * de forma dinámica sin necesidad de Schema Registry.
 */
public class AvroSerializer<T extends SpecificRecord> implements Serializer<T> {

    public static final String AVRO_TYPE_HEADER = "avro.type";

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No requiere configuración adicional
    }

    @Override
    public byte[] serialize(String topic, T data) {
        return serializeAvro(data);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, T data) {
        if (data == null) {
            return null;
        }
        if (headers != null) {
            headers.add(new RecordHeader(AVRO_TYPE_HEADER,
                    data.getClass().getName().getBytes(StandardCharsets.UTF_8)));
        }
        return serializeAvro(data);
    }

    @SuppressWarnings("unchecked")
    private byte[] serializeAvro(T data) {
        if (data == null) {
            return null;
        }
        try {
            SpecificDatumWriter<T> writer = new SpecificDatumWriter<>(data.getSchema());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            writer.write(data, encoder);
            encoder.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(
                    "Error serializing Avro message of type " + data.getClass().getName(), e);
        }
    }

    @Override
    public void close() {
        // No-op
    }
}
