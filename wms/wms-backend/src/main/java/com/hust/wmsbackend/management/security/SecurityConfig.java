package com.hust.wmsbackend.management.security;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@AllArgsConstructor(onConstructor_ = @Autowired)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private BasicAuthenticationEndPoint basicAuthenticationEndPoint;

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        Jwt2AuthenticationConverter jwtConverter = new Jwt2AuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(new Jwt2AuthoritiesConverter());
        jwtConverter.setPrincipalClaimName("preferred_username");

        return jwtConverter;
    }

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
        // State-less session (state in access-token only)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();

        http
                .authorizeRequests()
//                .antMatchers("/wmsv2/**").permitAll()
//                .antMatchers("/entity-authorization/**").permitAll()
//                .antMatchers("/user/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .requestCache()
                .requestCache(new NullRequestCache())
                .and() // Not cache request because of having frontend
                .httpBasic()
                .authenticationEntryPoint(basicAuthenticationEndPoint)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

    }
}
