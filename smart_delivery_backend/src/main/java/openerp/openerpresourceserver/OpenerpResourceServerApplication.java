package openerp.openerpresourceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@SpringBootApplication
@EnableJpaAuditing
public class OpenerpResourceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenerpResourceServerApplication.class, args);
    }

}
