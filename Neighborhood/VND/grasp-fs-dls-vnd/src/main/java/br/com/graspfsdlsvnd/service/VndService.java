package br.com.graspfsdlsvnd.service;

import br.com.graspfsdlsvnd.enuns.LocalSearch;
import br.com.graspfsdlsvnd.producer.KafkaBitFlipProducer;
import org.springframework.stereotype.Service;

@Service
public class VndService {

    KafkaBitFlipProducer bitFlipProducer;

    public void doVnd(String data, LocalSearch localSearch) {
//        //logica vnd
//        if (localSearch == LocalSearch.BIT_FLIP) {
//            bitFlipProducer.send(data);
//        }
    }
}
