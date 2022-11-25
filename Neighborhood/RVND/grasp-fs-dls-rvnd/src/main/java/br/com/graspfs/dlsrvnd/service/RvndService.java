package br.com.graspfs.dlsrvnd.service;

import br.com.graspfs.dlsrvnd.dto.DataSolution;
import br.com.graspfs.dlsrvnd.enuns.LocalSearch;
import br.com.graspfs.dlsrvnd.producer.KafkaBitFlipProducer;
import br.com.graspfs.dlsrvnd.producer.KafkaInitialSolutionProducer;
import br.com.graspfs.dlsrvnd.producer.KafkaIwssProducer;
import br.com.graspfs.dlsrvnd.producer.KafkaIwssrProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RvndService {

    private final Logger logg = LoggerFactory.getLogger(KafkaBitFlipProducer.class);

    @Autowired
    KafkaBitFlipProducer bitFlipProducer;
    @Autowired
    KafkaIwssProducer kafkaIwssProducer;
    @Autowired
    KafkaIwssrProducer kafkaIwssrProducer;

    @Autowired
    KafkaInitialSolutionProducer kafkaInitialSolutionProducer;
    private Integer iteration = 1;

    public void doRvnd(DataSolution data) {
//        //logica vnd
        data.setNeighborhood("rvnd");
        data.setIterationNeighborhood(iteration);

        iteration++;
        var random = new Random();
        switch (random.nextInt(3)){
            case 1:
                logg.info("SEND BITF");
                bitFlipProducer.send(data);
                break;
            case 2:
                logg.info("SEND IWSS");
                kafkaIwssProducer.send(data);
                break;
            case 3:
                logg.info("SEND IWSSR");
                kafkaIwssrProducer.send(data);
                break;
        }
    }
}
