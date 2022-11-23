package br.com.graspfs.ls.iwss.consumer;

import br.com.graspfs.ls.iwss.dto.DataSolution;
import br.com.graspfs.ls.iwss.service.IwssService;
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
public class KafkaIwssConsumer {

    IwssService iwssService;

    private final Logger logg = LoggerFactory.getLogger(KafkaIwssConsumer.class);

    @KafkaListener(topics = "BIT_FLIP_TOPIC", groupId = "myGroup", containerFactory = "bitFlipListenerContainerFactory")
    public void consume(ConsumerRecord<String, DataSolution> record){

        logg.info("Received Message " + record.value());
        final var time = System.currentTimeMillis();
        try{
            iwssService.doIwss(record.value());
        }catch(IllegalArgumentException ex){
            throw ex;
        }
    }

}
