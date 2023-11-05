package com.hust.openerp.taskmanagement.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

import com.hust.openerp.taskmanagement.exception.CustomAccessDeniedHandler;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig {

  /**
   * OK
   *
   * @return
   */
  @Bean
  Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
    final var jwtConverter = new Jwt2AuthenticationConverter();

    jwtConverter.setJwtGrantedAuthoritiesConverter(new Jwt2AuthoritiesConverter());
    jwtConverter.setPrincipalClaimName("preferred_username");

    return jwtConverter;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .oauth2ResourceServer(
            cfg -> cfg.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .sessionManagement(
            sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/roles").permitAll())
        .authorizeHttpRequests(
            authorizeRequests -> authorizeRequests.requestMatchers("/actuator/prometheus/**").permitAll())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/videos/videos/*").permitAll())
        .authorizeHttpRequests(
            authorizeRequests -> authorizeRequests.requestMatchers("/resources/**").permitAll())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/css/**").permitAll())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/image/**").permitAll())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/js/**").permitAll())
        .authorizeHttpRequests(
            authorizeRequests -> authorizeRequests.requestMatchers("/export-problem/*").permitAll())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/v2/api-docs").permitAll())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/swagger-ui").permitAll())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/public/**").permitAll())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/nghialm").permitAll())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
        .requestCache(requestCache -> requestCache.requestCache(new NullRequestCache()))
        .httpBasic(AbstractHttpConfigurer::disable)
        .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(accessDeniedHandler()))
        .headers(headers -> headers.frameOptions(option -> option.disable()));

    return http.build();
  }

  @Bean
  CustomAccessDeniedHandler accessDeniedHandler() {
    return new CustomAccessDeniedHandler();
  }

}
