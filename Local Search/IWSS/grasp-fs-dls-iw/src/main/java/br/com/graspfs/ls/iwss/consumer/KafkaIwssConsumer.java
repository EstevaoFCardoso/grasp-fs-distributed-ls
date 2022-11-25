package br.com.graspfs.ls.iwss.consumer;

import br.com.graspfs.ls.iwss.dto.DataSolution;
import br.com.graspfs.ls.iwss.service.IwssService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaIwssConsumer {

    @Autowired
    IwssService iwssService;

    private final Logger logg = LoggerFactory.getLogger(KafkaIwssConsumer.class);

    @KafkaListener(topics = "IWSS_TOPIC", groupId = "myGroup")
    public void consume(DataSolution record){

        logg.info("Received Message in IWSS" + record);
        final var time = System.currentTimeMillis();
        try{
            iwssService.doIwss(record,time);
        }catch(IllegalArgumentException ex){
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
