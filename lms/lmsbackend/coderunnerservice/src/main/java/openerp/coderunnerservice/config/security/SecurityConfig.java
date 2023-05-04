package openerp.coderunnerservice.config.security;

import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter(
    ) {
        final var jwtConverter = new Jwt2AuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(new Jwt2AuthoritiesConverter());
        jwtConverter.setPrincipalClaimName("preferred_username");

        return jwtConverter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // Enable OAuth2 with custom authorities mapping
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());

//        // Enable anonymous
//        http.anonymous();

        // State-less session (state in access-token only)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Disable CSRF because of state-less session-management
        http.csrf().disable();

//        // Return 401 (unauthorized) instead of 302 (redirect to login) when authorization is missing or invalid
//        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
//            response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Restricted Content\"");
//            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
//        });
//
//        // If SSL enabled, disable http (https only)
//        if (serverProperties.getSsl() != null && serverProperties.getSsl().isEnabled()) {
//            http.requiresChannel().anyRequest().requiresSecure();
//        }

        // Route security
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .requestCache()
                .requestCache(new NullRequestCache()) // Not cache request because of having frontend
                .and()
                .httpBasic()
                .disable()
                .headers()
                .frameOptions()
                .disable();
    }
}


