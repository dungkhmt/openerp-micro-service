package openerp.openerpresourceserver.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
public class SecurityConfig {

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter(
    ) {
        final var jwtConverter = new Jwt2AuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(new Jwt2AuthoritiesConverter());
        jwtConverter.setPrincipalClaimName("preferred_username");

        return jwtConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Enable OAuth2 with custom authorities mapping
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());


        // State-less session (state in access-token only)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Disable CSRF because of state-less session-management
        http.csrf().disable();

        // Route security
        http
                .authorizeHttpRequests()
                    .requestMatchers("/student-statistics/student-performance/**")
                    .hasAnyRole("TEACHER", "STUDENT")
                    .requestMatchers("/student-statistics/details/**")
                    .hasAnyRole("TEACHER", "STUDENT")
                    .requestMatchers("/student-statistics/student-contest-statistic/**")
                    .hasAnyRole("TEACHER", "STUDENT")
                    .requestMatchers("/get-all")
                    .hasAnyRole("TEACHER")
                    .anyRequest().authenticated()
                    .and()
                .requestCache()
                    .requestCache(new NullRequestCache()) // Not cache request because of having frontend
                    .and()
                .httpBasic().disable()
                .headers()
                    .frameOptions().disable();

        return http.build();
    }
}


