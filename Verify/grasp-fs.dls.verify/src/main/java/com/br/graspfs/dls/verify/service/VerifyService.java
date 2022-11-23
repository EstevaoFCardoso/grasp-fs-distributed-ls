package com.br.graspfs.dls.verify.service;

import com.br.graspfs.dls.verify.consumer.KafkaBestSolutionConsumer;
import com.br.graspfs.dls.verify.dto.DataSolution;
import com.br.graspfs.dls.verify.producer.KafkaSolutionsProducer;
import org.springframework.stereotype.Service;

@Service
public class VerifyService {

    KafkaSolutionsProducer kafkaSolutionsProducer;
    KafkaBestSolutionConsumer kafkaBestSolutionConsumer;

    public void doVerify(DataSolution data) {
        DataSolution bestSolution = kafkaBestSolutionConsumer.consume();
    }
}
