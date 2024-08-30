package com.heima.kafka.sample;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamQuickStart {

    public static void main(String[] args) {
        //kafka的配置信心
        Properties prop = new Properties();
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.50.128:9092");
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG,"streams-quickstart");

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //流式计算
        streamprocessor(streamsBuilder);

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), prop);
        kafkaStreams.start();
    }

    private static void streamprocessor(StreamsBuilder streamsBuilder) {

        //创建kstream对象，同时指定从那个topic中接收消息
        KStream<String, String> stream = streamsBuilder.stream("topic-input");

        /**
         * 处理消息的value
         */

        stream.flatMapValues((ValueMapper<String, Iterable<String>>) value -> Arrays.asList(value.split(" ")))

                .groupBy((key,value) -> value)

                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                .count()
                .toStream()
                .map((key,value) ->{
                    System.out.println("key:"+key+",vlaue:"+value);
                    return new KeyValue<>(key.key().toString(),value.toString());
                })
                //发送消息
                .to("topic-out");
    }
}
