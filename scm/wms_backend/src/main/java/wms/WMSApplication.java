package wms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import wms.algorithms.utils.Utils;


@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@SpringBootApplication
@Slf4j
@EnableScheduling
public class WMSApplication {

    public static void main(String[] args) {
        SpringApplication.run(WMSApplication.class, args);
//        double dist = Utils.calculateCoordinationDistance(20.99121179567029, 105.83914145287527, 21.030375443054634, 105.7834482118775);
//        log.info("Dist {}", dist);
        log.info("Swagger link: http://localhost:8080/api/swagger-ui.html#/");
        log.info("Swagger docs: http://localhost:8080/api/v2/api-docs");
//        double newDist = Utils.getDistanceGraphhopperApi(20.99121179567029, 105.83914145287527, 21.030375443054634, 105.7834482118775);
//        log.info("New dist {}", newDist);
    }

}
