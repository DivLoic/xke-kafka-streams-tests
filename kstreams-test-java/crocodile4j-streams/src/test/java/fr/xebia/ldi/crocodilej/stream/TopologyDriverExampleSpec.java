package fr.xebia.ldi.crocodilej.stream;

import fr.xebia.ldi.crocodilej.stream.operator.Mappers;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import static org.apache.kafka.common.serialization.Serdes.*;

import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 * Created by loicmdivad.
 */
class TopologyDriverExampleSpec {

    private static Properties properties = new Properties();
    private static Consumed<String, String> consumed = Consumed.with(String(), String());

    private static ConsumerRecordFactory<String, String> recordFactory =
            new ConsumerRecordFactory<String, String>(String().serializer(), String().serializer());

    @BeforeAll
    static void setUp() {
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "topology-driver-example");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "uselesshost:0000");
    }

    @Test
    void getPriceMapAJSONToInteger() {
        StreamsBuilder builder = new StreamsBuilder();

        // Given
        ConsumerRecord<byte[], byte[]> input =
                recordFactory.create("input-topic", "12345", "{'item':'#1','price':90.99}");

        builder.stream("input-topic", consumed)
                .mapValues(Mappers.getPrice)
                .to("output-topic");

        TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), properties);

        // When
        testDriver.pipeInput(input);

        // Then
        ProducerRecord<String, Integer> result =
                testDriver.readOutput("output-topic", String().deserializer(), Integer().deserializer());

        OutputVerifier.compareKeyValue(result, "12345", 9099);
    }
}
