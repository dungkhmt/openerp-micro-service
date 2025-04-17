package openerp.openerpresourceserver.config.security;

import openerp.openerpresourceserver.enums.RoleEnum;
import openerp.openerpresourceserver.repo.EmployeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final EmployeeRepository employeeRepository;

    public SecurityConfig(EmployeeRepository userRepository) {
        this.employeeRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/admin/**").hasRole(RoleEnum.ADMIN.name())
                        .requestMatchers("/manager/**").hasRole(RoleEnum.MANAGER.name())
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    @Bean
    public Jwt2AuthenticationConverter jwtAuthenticationConverter() {
        Jwt2AuthenticationConverter converter = new Jwt2AuthenticationConverter(employeeRepository);
        converter.setJwtGrantedAuthoritiesConverter(new Jwt2AuthoritiesConverter());
        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }
}