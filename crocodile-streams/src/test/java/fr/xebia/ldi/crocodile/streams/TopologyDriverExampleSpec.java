package fr.xebia.ldi.crocodile.streams;

import fr.xebia.ldi.crocodile.streams.operator.Mappers;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by loicmdivad.
 */
class TopologyDriverExampleSpec {

    private static Properties properties = new Properties();
    private static Consumed<String, String> consumed = Consumed.with(Serdes.String(), Serdes.String());
    private static Produced<String, Integer> produced = Produced.with(Serdes.String(), Serdes.Integer());

    private static ConsumerRecordFactory<String, String> recordFactory =
            new ConsumerRecordFactory<>(Serdes.String().serializer(), Serdes.String().serializer());

    @BeforeAll
    static void setUp() {
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "topology-driver-example");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "uselesshost:0000");
    }

    @Test
    void getPriceShouldMapAJSONToInteger() {
        StreamsBuilder builder = new StreamsBuilder();

        // Given
        ConsumerRecord<byte[], byte[]> input =
                recordFactory.create("input-topic", "12345", "{'item':'#1','price':90.99}");

        builder.stream("input-topic", consumed)
                .mapValues(Mappers.getPrice)
                .to("output-topic", produced);

        TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), properties);

        // When
        testDriver.pipeInput(input);

        // Then
        ProducerRecord<String, Integer> result =
                testDriver.readOutput("output-topic", Serdes.String().deserializer(), Serdes.Integer().deserializer());

        OutputVerifier.compareKeyValue(result, "12345", 9099);
    }

    @Test
    void getPriceShouldReturnMinusOneOnAnyExtractionProblem() {
        StreamsBuilder builder = new StreamsBuilder();

        // Given
        List<ConsumerRecord<byte[], byte[]>> inputs = Arrays.asList(
                recordFactory.create("input-topic", "101", "{'item':'#1'}"),
                recordFactory.create("input-topic", "102", "{'item':'#1','price':'a lot of money'}"),
                recordFactory.create("input-topic", "103", "NOT EVEN A JSON ... ¯\\_(ツ)_//¯")
        );

        builder.stream("input-topic", consumed)
                .mapValues(Mappers.getPrice)
                .to("output-topic", produced);

        TopologyTestDriver testDriver = new TopologyTestDriver(builder.build(), properties);

        // When
        inputs.forEach(testDriver::pipeInput);

        // Then
        ProducerRecord<String, Integer> result1 =
                testDriver.readOutput("output-topic", Serdes.String().deserializer(), Serdes.Integer().deserializer());

        ProducerRecord<String, Integer> result2 =
                testDriver.readOutput("output-topic", Serdes.String().deserializer(), Serdes.Integer().deserializer());

        ProducerRecord<String, Integer> result3 =
                testDriver.readOutput("output-topic", Serdes.String().deserializer(), Serdes.Integer().deserializer());

        OutputVerifier.compareKeyValue(result1, "101", -1);
        OutputVerifier.compareKeyValue(result2, "102", -1);
        OutputVerifier.compareKeyValue(result3, "103", -1);
    }
}
