package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerQuickStart {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1.kafka的配置信息
        Properties properties = new Properties();
        //kafka的连接地址

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.50.128:9092");
        //发送失败，失败的重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG,5);
        //消息key的序列化器
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        //消息value的序列化器
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        //2.生产者对象
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);

        for (int i = 0; i < 5; i++) {
            //封装发送的消息
            ProducerRecord<String,String> record = new ProducerRecord<String, String>("topic-input","hello kafka");
            producer.send(record);
            ////3.发送消息
            ////异步发送
            //producer.send(record, (recordMetadata, e) -> {
            //    if (e != null) {
            //        System.out.println("记录异常" + e);
            //    }
            //
            //    System.out.println(recordMetadata.offset());
            //});
        }

        //4.关闭消息通道，必须关闭，否则消息发送不成功
        producer.close();
    }
}
