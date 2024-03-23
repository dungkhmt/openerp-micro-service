package com.example.api.configs.security;

import com.example.api.configs.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.api.configs.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.example.api.configs.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.example.api.services.auth.CustomOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthConfig {
    private final SecurityFilter securityFilter;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final CustomOAuth2Service oAuth2Service;
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/public/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/api/v1/admin/*").hasRole("ADMIN") // it will throw error before my exception handler catch it
//                .requestMatchers(HttpMethod.GET, "/api/v1/admin/*").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.GET, "/api/v1/client/*").hasRole("CLIENT")
//                .requestMatchers(HttpMethod.POST, "/api/v1/client/*").hasRole("CLIENT")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2Login -> oauth2Login
                .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                    .baseUri("/oauth2/authorize")
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                )
                .redirectionEndpoint(redirectionEndpoint -> redirectionEndpoint
                    .baseUri("/oauth2/callback/*")
                )
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                    .userService(oAuth2Service)
                )
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
