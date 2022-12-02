package br.com.graspfs.ls.bf.consumer;

import br.com.graspfs.ls.bf.dto.DataSolution;
import br.com.graspfs.ls.bf.service.BitFlipService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaBitFlipConsumer {

    @Autowired
    BitFlipService bitFlipService;

    private final Logger logg = LoggerFactory.getLogger(KafkaBitFlipConsumer.class);

    @KafkaListener(topics = {"BIT_FLIP_TOPIC"}, groupId = "myGroup")
    public void consume(DataSolution record){
        logg.info("Received Message " + record);
        try{
            bitFlipService.doBipFlip(record);
        }catch(IllegalArgumentException ex){
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
