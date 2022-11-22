package br.com.graspfsdlsvnd.controller;

import br.com.graspfsdlsvnd.dto.DataSolution;
import br.com.graspfsdlsvnd.producer.KafkaBitFlipProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
@RequestMapping("/bit-flip")
@RequiredArgsConstructor
public class BitFlipControler {

    private final KafkaBitFlipProducer bitFlipProducer;


    @PostMapping
    public ResponseEntity<DataSolution> createMessage(){
        var data = DataSolution.builder()
                .id("VND")
                .rclfeatures(new ArrayList<>(1))
                .solutionFeatures(new ArrayList<>(1))
                .build();
        bitFlipProducer.send(data);
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

}
