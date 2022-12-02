package br.com.graspfs.ls.iwssr.consumer;

import br.com.graspfs.ls.iwssr.dto.DataSolution;
import br.com.graspfs.ls.iwssr.service.IwssrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaIwssrConsumer {

    @Autowired
    IwssrService iwssrService;

    private final Logger logg = LoggerFactory.getLogger(KafkaIwssrConsumer.class);

    @KafkaListener(topics = "IWSSR_TOPIC", groupId = "myGroup")
    public void consume(ConsumerRecord<String, DataSolution> record){

        logg.info("Received Message " + record.value());

        try{
            iwssrService.doIwssr(record.value());
        }catch(IllegalArgumentException ex){
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}