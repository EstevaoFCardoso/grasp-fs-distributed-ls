package br.com.graspfsdlsvnd.producer;

import br.com.graspfsdlsvnd.dto.DataSolution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaBitFlipProducer {

    private final String topic;
    private final Logger logg = LoggerFactory.getLogger(KafkaBitFlipProducer.class);
    private final KafkaTemplate<String, DataSolution> kafkaTemplate;

    public KafkaBitFlipProducer(@Value("${topic.name}")String topic, KafkaTemplate<String, DataSolution> kafkaTemplate){
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(DataSolution data){
        kafkaTemplate.send(topic,data).addCallback(
                sucess -> {
                    assert sucess != null;
                    logg.info("Mensage send sucess " + sucess.getProducerRecord().value());
                },
                failure -> logg.info("Mensage Failure " + failure.getMessage())
        );
    }

}
