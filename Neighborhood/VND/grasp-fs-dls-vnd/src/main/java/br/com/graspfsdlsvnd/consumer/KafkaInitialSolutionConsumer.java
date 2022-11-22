package br.com.graspfsdlsvnd.consumer;

import br.com.graspfsdlsvnd.enuns.LocalSearch;
import br.com.graspfsdlsvnd.service.VndService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaInitialSolutionConsumer {

    VndService vndService;

    @KafkaListener(topics = "teste", groupId = "teste")
    public void consume(String data){

        try{
            if(!data.isEmpty()){
                vndService.doVnd(data, LocalSearch.BIT_FLIP);
            }else{
                throw new IllegalArgumentException();
            }
        }catch(IllegalArgumentException ex){
            throw ex;
        }
    }

}
