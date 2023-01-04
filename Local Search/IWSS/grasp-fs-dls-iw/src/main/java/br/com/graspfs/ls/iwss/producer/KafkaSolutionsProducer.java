package br.com.graspfs.ls.iwss.producer;

import br.com.graspfs.ls.iwss.dto.DataSolution;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaSolutionsProducer {

    private final String topic;
    private final Logger logg = LoggerFactory.getLogger(KafkaSolutionsProducer.class);
    @Autowired
    private final KafkaTemplate<String, DataSolution> kafkaTemplate;

    public KafkaSolutionsProducer(KafkaTemplate<String, DataSolution> kafkaTemplate){
        this.topic = "SOLUTION_TOPIC";
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
