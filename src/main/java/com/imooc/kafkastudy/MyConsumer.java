package com.imooc.kafkastudy;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class MyConsumer {
    private static KafkaConsumer<String,String> consumer;
    private static Properties properties;
    static{
        properties = new Properties();
        properties.put("bootstrap.servers","127.0.0.1:9092");
        properties.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id","KafkaStudy");
        consumer = new KafkaConsumer<String, String>(properties);
    }
    private static void generalConsumeMessageAutoCommit(){
        properties.put("enable.auto.commit",true);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singleton("imooc-kafka-study"));
        try{
            while(true){
                boolean flag = true;
                ConsumerRecords<String,String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(String.format("topic:%s,partition:%s,key:%s,value:%s",
                            record.topic(),
                            record.partition(),
                            record.key(),
                            record.value()));
                    if(record.value().equals("done")) {
                        flag = false;
                    }
                }
                if(!flag)break;
            }

        }finally {
            consumer.close();
        }

    }

    // Sync commit will block the thread, but it will return error if commit went wrong.
    private static void generalConsumeMessageSyncCommit(){
        properties.put("auto.commit.offset",false);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singleton("imooc-kafka-study"));
        try{
            while(true){
                boolean flag = true;
                ConsumerRecords<String,String> records = consumer.poll(100);
                for(ConsumerRecord<String,String> record:records){
                    System.out.println(String.format("topic:%s,partition:%s,key:%s,value:%s",
                            record.topic(),
                            record.partition(),
                            record.key(),
                            record.value()));
                    if(record.value().equals("done")) {
                        flag = false;
                    }
                }
                try{
                    consumer.commitSync();
                }catch (CommitFailedException ex){
                    ex.printStackTrace();
                }
                if(!flag)break;
            }

        }finally {
            consumer.close();;
        }
    }

    // much faster than Sync commit but we know nothing about the result.
    private static void generalConsumeMessageAsyncCommit(){
        properties.put("auto.commit.offset",false);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singleton("imooc-kafka-study"));
        try{
            while(true){
                boolean flag = true;
                ConsumerRecords<String,String> records = consumer.poll(100);
                for(ConsumerRecord<String,String> record:records){
                    System.out.println(String.format("topic:%s,partition:%s,key:%s,value:%s",
                            record.topic(),
                            record.partition(),
                            record.key(),
                            record.value()));
                    if(record.value().equals("done")) {
                        flag = false;
                    }
                }
                try{
                    consumer.commitAsync();
                }catch (CommitFailedException ex){
                    ex.printStackTrace();
                }
                if(!flag)break;
            }

        }finally {
            consumer.close();;
        }
    }

    private static void generalConsumeMessageAsyncCommitWithCallback(){
        properties.put("auto.commit.offset",false);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singleton("imooc-kafka-study"));
        try{
            while(true){
                boolean flag = true;
                ConsumerRecords<String,String> records = consumer.poll(100);
                for(ConsumerRecord<String,String> record:records){
                    System.out.println(String.format("topic:%s,partition:%s,key:%s,value:%s",
                            record.topic(),
                            record.partition(),
                            record.key(),
                            record.value()));
                    if(record.value().equals("done")) {
                        flag = false;
                    }
                }
                try{
                    consumer.commitAsync((map, e) -> {
                        if(e != null){
                            System.out.println("System fail to commit at offset:"+e.getMessage());
                        }
                    });
                }catch (CommitFailedException ex){
                    ex.printStackTrace();
                }
                if(!flag)break;
            }

        }finally {
            consumer.close();;
        }
    }

    private static void mixSyncAndAsyncCommit(){
        properties.put("auto.commit.offset",false);
        consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Collections.singleton("imooc-kafka-study"));
        try{
            while(true) {
                boolean flag = true;
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(String.format("topic:%s,partition:%s,key:%s,value:%s",
                            record.topic(),
                            record.partition(),
                            record.key(),
                            record.value()));
                    if(record.value().equals("done")) {
                        flag = false;
                    }
                }
                if(!flag)break;
            }
        }catch (CommitFailedException ex){
            System.out.println("Commit fail at offset:"+ex.getMessage());
        }finally {
            try{
                consumer.commitSync();
            }finally {
                consumer.close();
            }
        }
    }

    public static void main(String[] args) {
        generalConsumeMessageAutoCommit();
    }
}
