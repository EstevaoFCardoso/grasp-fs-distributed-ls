package br.com.graspfsdlsvnd.service;

import br.com.graspfsdlsvnd.dto.DataSolution;
import br.com.graspfsdlsvnd.enuns.LocalSearch;
import br.com.graspfsdlsvnd.producer.KafkaBitFlipProducer;
import br.com.graspfsdlsvnd.producer.KafkaIwssProducer;
import br.com.graspfsdlsvnd.producer.KafkaIwssrProducer;
import org.springframework.stereotype.Service;

@Service
public class VndService {

    KafkaBitFlipProducer bitFlipProducer;
    KafkaIwssProducer kafkaIwssProducer;
    KafkaIwssrProducer kafkaIwssrProducer;
    private Integer iteration = 1;

    public void doVnd(DataSolution data, LocalSearch localSearch) {
//        //logica vnd
        data.setNeighborhood("vnd");
        data.setIterationNeighborhood(iteration);
        iteration++;
        switch (localSearch){
            case BIT_FLIP:
                bitFlipProducer.send(data);
                break;
            case IWSS:
                kafkaIwssProducer.send(data);
                break;
            case IWSSR:
                kafkaIwssrProducer.send(data);
                break;
        }
    }
}
