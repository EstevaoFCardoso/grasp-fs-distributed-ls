package br.com.graspfsdlsvnd;

import br.com.graspfsdlsvnd.config.KafkaProducerConfig;
import br.com.graspfsdlsvnd.dto.DataSolution;
import br.com.graspfsdlsvnd.producer.KafkaBitFlipProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;

@SpringBootApplication
public class GraspFsDlsVndApplication {

    public static void main(String[] args) {
		SpringApplication.run(GraspFsDlsVndApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(KafkaBitFlipProducer bitFlipProducer){
        var data = DataSolution.builder()
                .id("VND")
                .rclfeatures(new ArrayList<>(1))
                .solutionFeatures(new ArrayList<>(1))
                .build();
        return args -> {
            bitFlipProducer.send(data);
        };
    }

}
