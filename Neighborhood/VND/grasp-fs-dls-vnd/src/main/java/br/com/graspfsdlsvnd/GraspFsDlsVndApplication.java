package br.com.graspfsdlsvnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraspFsDlsVndApplication {

    public static void main(String[] args) {
		SpringApplication.run(GraspFsDlsVndApplication.class, args);
    }
//    @Bean
//    CommandLineRunner commandLineRunner(KafkaBitFlipProducer bitFlipProducer){
//        var data = DataSolution.builder()
//                .id("VND")
//                .rclfeatures(new ArrayList<>(1))
//                .solutionFeatures(new ArrayList<>(1))
//                .build();
//        return args -> {
//            bitFlipProducer.send(data);
//        };
//    }
}
