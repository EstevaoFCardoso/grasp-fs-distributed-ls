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
                logg.info("SEND IWSS");
                kafkaIwssProducer.send(data);
                break;
            case IWSSR:
                logg.info("SEND IWSSR");
                kafkaIwssrProducer.send(data);
                break;
        }
    }

    public DataSolution callNextService(DataSolution bestSolution, ConsumerRecord<String, DataSolution> record ) {
        logg.info("CALLNEXTSERVICE");
        if (record.value().getF1Score() > bestSolution.getF1Score()) {
            logg.info("RESET");
            kafkaInitialSolutionProducer.send(record.value());
            return record.value();
        }else {
            // proximo
            if (record.value().getLocalSearch().equals(LocalSearchUtils.BF)) {
                logg.info("SEND IWSS CALL");
                doVnd(record.value(), LocalSearch.IWSS);
            } else if (record.value().getLocalSearch().equals(LocalSearchUtils.IW)) {
                logg.info("SEND IWSSR CALL");
                doVnd(record.value(), LocalSearch.IWSSR);
            } else if (record.value().getLocalSearch().equals(LocalSearchUtils.IWR)) {
                logg.info("SEND BITF CALL");
                doVnd(record.value(), LocalSearch.BIT_FLIP);
            } else {
                throw new IllegalArgumentException("ERROR");
            }
        }
        return bestSolution;
    }

}
