package br.com.graspfsdlsvnd.service;

import br.com.graspfsdlsvnd.dto.DataSolution;
import br.com.graspfsdlsvnd.enuns.LocalSearch;
import br.com.graspfsdlsvnd.producer.KafkaBitFlipProducer;
import br.com.graspfsdlsvnd.producer.KafkaInitialSolutionProducer;
import br.com.graspfsdlsvnd.producer.KafkaIwssProducer;
import br.com.graspfsdlsvnd.producer.KafkaIwssrProducer;
import br.com.graspfsdlsvnd.util.LocalSearchUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VndService {

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

    public void doVnd(DataSolution data, LocalSearch localSearch) {
//        //logica vnd
        data.setNeighborhood("vnd");
        data.setIterationNeighborhood(iteration);
        iteration++;
        switch (localSearch){
            case BIT_FLIP:
                logg.info("SEND BITF");
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

    public DataSolution callNextService(DataSolution bestSolution, ConsumerRecord<String, DataSolution> record ) {
        if (bestSolution.getF1Score() > record.value().getF1Score()) {
            // proximo
            if (record.value().getLocalSearch().equals(LocalSearchUtils.BF)) {
                doVnd(record.value(), LocalSearch.IWSS);
            } else if (record.value().getLocalSearch().equals(LocalSearchUtils.IW)) {
                doVnd(record.value(), LocalSearch.IWSSR);
            } else if (record.value().getLocalSearch().equals(LocalSearchUtils.IWR)) {
                doVnd(record.value(), LocalSearch.BIT_FLIP);
            } else {
                throw new IllegalArgumentException("ERROR");
            }
        }else {
            kafkaInitialSolutionProducer.send(record.value());
            return record.value();
        }
        return bestSolution;
    }

}
