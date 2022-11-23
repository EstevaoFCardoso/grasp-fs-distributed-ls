package br.com.graspfs.ls.bf.consumer;

import br.com.graspfs.ls.bf.dto.DataSolution;
import br.com.graspfs.ls.bf.service.BitFlipService;
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
public class KafkaBitFlipConsumer {

    BitFlipService bitFlipService;

    private final Logger logg = LoggerFactory.getLogger(KafkaBitFlipConsumer.class);

    @KafkaListener(topics = "BIT_FLIP_TOPIC", groupId = "myGroup")
    public void consume(ConsumerRecord<String, DataSolution> record){

        logg.info("Received Message " + record.value());
        final var timeConsume = System.currentTimeMillis();
        try{
            bitFlipService.doBipFlip(record.value(), timeConsume);
        }catch(IllegalArgumentException ex){
            throw ex;
        }
    }

}
