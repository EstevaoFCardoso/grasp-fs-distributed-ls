package br.com.graspfsdlsvnd.config;

import br.com.graspfsdlsvnd.dto.DataSolution;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapserver;
    

    public Map<String, Object> kafkaConsumerConfig(){
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapserver);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return properties;
    }

    @Bean
    public ConsumerFactory<String, DataSolution> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfig());
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DataSolution> dataSolutionKafkaTemplateConsumer(){
        ConcurrentKafkaListenerContainerFactory<String, DataSolution> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(consumerFactory());
        return containerFactory;
    }

}
