package br.com.graspfsdlsvnd.producer;

import br.com.graspfsdlsvnd.dto.DataSolution;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaBitFlipProducer {

    private final String topic;
    private final Logger logg = LoggerFactory.getLogger(KafkaBitFlipProducer.class);

    @Autowired
    private final KafkaTemplate<String, DataSolution> kafkaTemplate;

    public KafkaBitFlipProducer(KafkaTemplate<String, DataSolution> kafkaTemplate){
        this.topic = "BIT_FLIP_TOPIC";
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(DataSolution data){
        kafkaTemplate.send(topic, data).addCallback(
                sucess -> {
                    assert sucess != null;
                    logg.info("Mensage send sucess " + sucess.getProducerRecord().value());
                    logg.info("HEADERS" + sucess.getProducerRecord().headers());
                },
                failure -> logg.info("Mensage Failure " + failure.getMessage())
        ); 
    }

}
