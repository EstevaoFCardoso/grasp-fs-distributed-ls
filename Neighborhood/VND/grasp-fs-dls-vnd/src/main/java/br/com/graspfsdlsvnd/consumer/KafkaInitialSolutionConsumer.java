package br.com.graspfsdlsvnd.consumer;

import br.com.graspfsdlsvnd.dto.DataSolution;
import br.com.graspfsdlsvnd.enuns.LocalSearch;
import br.com.graspfsdlsvnd.service.VndService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaInitialSolutionConsumer {

    @Autowired
    VndService vndService;

    private final Logger logg = LoggerFactory.getLogger(KafkaInitialSolutionConsumer.class);

    @KafkaListener(topics = {"INITIAL_SOLUTION_TOPIC"}, groupId = "myGroup", containerFactory = "jsonKafkaListenerContainer")
    public void consume(ConsumerRecord<String, DataSolution> record){

        logg.info("Received Message " + record.value());
        final var time = System.currentTimeMillis();

        try{
            vndService.doVnd(record.value(), LocalSearch.BIT_FLIP);
        }catch(Exception ex){
            throw ex;
        }
    }

}
