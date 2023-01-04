package br.com.graspfs.dlsrvnd.consumer;

import br.com.graspfs.dlsrvnd.dto.DataSolution;
import br.com.graspfs.dlsrvnd.enuns.LocalSearch;
import br.com.graspfs.dlsrvnd.service.RvndService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class KafkaInitialSolutionConsumer {

    @Autowired
    RvndService rvndService;

    private final Logger logg = LoggerFactory.getLogger(KafkaInitialSolutionConsumer.class);

    @KafkaListener(topics = {"INITIAL_SOLUTION_TOPIC"}, groupId = "myGroup", containerFactory = "jsonKafkaListenerContainer")
    public void consume(ConsumerRecord<String, DataSolution> record){

        logg.info("Received Message " + record.value());
        final var time = System.currentTimeMillis();
        for(int i =0;i<10;i++){
            try{
                rvndService.doRvnd(record.value());
            }catch(Exception ex){
                throw ex;
            }
        }

    }

}
