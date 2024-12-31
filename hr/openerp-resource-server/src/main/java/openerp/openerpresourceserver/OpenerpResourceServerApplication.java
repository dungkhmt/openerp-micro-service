package openerp.openerpresourceserver;

import openerp.openerpresourceserver.domain.common.DomainComponent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@SpringBootApplication
@ComponentScan(
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {DomainComponent.class})
        }
)
public class OpenerpResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenerpResourceServerApplication.class, args);
    }

}
