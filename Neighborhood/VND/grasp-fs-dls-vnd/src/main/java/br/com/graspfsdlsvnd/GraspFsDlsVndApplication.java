package br.com.graspfsdlsvnd;

import br.com.graspfsdlsvnd.dto.DataSolution;
import br.com.graspfsdlsvnd.producer.KafkaBitFlipProducer;
import br.com.graspfsdlsvnd.producer.KafkaInitialSolutionProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class GraspFsDlsVndApplication {

    public static void main(String[] args) {
		SpringApplication.run(GraspFsDlsVndApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(KafkaInitialSolutionProducer kafkaInitialSolutionProducer){
        var data = DataSolution.builder()
                .seedId(1L)
                .rclfeatures(new ArrayList(List.of(new Integer[]{6,
                        7,
                        8,
                        9,
                        10,
                        11,
                        12,
                        13,
                        14,
                        15,
                        16,
                        17,
                        18,
                        19,
                        20})))
                .solutionFeatures(new ArrayList(List.of(new Integer[]{6,
                        1,
                        2,
                        3,
                        4,
                        5})))
                .neighborhood("VND")
                .f1Score(0.78F)
                .runnigTime(84L)
                .iterationLocalSearch(0)
                .build();
        return args -> {
            kafkaInitialSolutionProducer.send(data);
        };
    }
}
