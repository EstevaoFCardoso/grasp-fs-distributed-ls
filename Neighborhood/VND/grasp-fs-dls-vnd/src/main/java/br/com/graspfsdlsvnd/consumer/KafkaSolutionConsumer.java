package br.com.graspfsdlsvnd.consumer;

import br.com.graspfsdlsvnd.dto.DataSolution;
import br.com.graspfsdlsvnd.enuns.LocalSearch;
import br.com.graspfsdlsvnd.producer.KafkaBitFlipProducer;
import br.com.graspfsdlsvnd.service.VndService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaSolutionConsumer {

    VndService vndService;

    private final Logger logg = LoggerFactory.getLogger(KafkaBitFlipProducer.class);

    @KafkaListener(topics = "SOLUTION_TOPIC", groupId = "myGroup", containerFactory = "initialSolutionListenerContainerFactory")
    public void consume(ConsumerRecord<String, DataSolution> record){

        logg.info("Received Message " + record.value());

        try{
            vndService.doVnd(record.value(), LocalSearch.BIT_FLIP);

        }catch(IllegalArgumentException ex){
            throw ex;
        }
    }

}
