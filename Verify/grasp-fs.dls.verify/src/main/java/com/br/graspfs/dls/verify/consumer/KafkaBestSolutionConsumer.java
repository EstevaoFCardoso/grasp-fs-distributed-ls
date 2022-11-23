package com.br.graspfs.dls.verify.consumer;

import com.br.graspfs.dls.verify.dto.DataSolution;
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
public class KafkaBestSolutionConsumer {

    private final Logger logg = LoggerFactory.getLogger(KafkaBestSolutionConsumer.class);

    @KafkaListener(topics = "BEST_SOLUTION_TOPIC", groupId = "myGroup", containerFactory = "bestSolutionListenerContainerFactory")
    public DataSolution consume(ConsumerRecord<String, DataSolution> record){

        logg.info("Received Message " + record.value());
        final var time = System.currentTimeMillis();
        try{
           return record.value();
        }catch(IllegalArgumentException ex){
            throw ex;
        }
    }

}
