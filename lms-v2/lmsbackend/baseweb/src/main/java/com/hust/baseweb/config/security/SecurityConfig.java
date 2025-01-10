package com.hust.baseweb.config.security;

import com.hust.baseweb.applications.education.exception.CustomAccessDeniedHandler;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.savedrequest.NullRequestCache;


@Configuration
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    private BaseWebUserDetailService userDetailsService;
//
//    private BasicAuthenticationEndPoint basicAuthenticationEndPoint;

    // TODO: consider
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(this.userDetailsService).passwordEncoder(UserLogin.PASSWORD_ENCODER);
//    }

    /**
     * OK
     *
     * @return
     */
    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter(
    ) {
        final var jwtConverter = new Jwt2AuthenticationConverter();

        jwtConverter.setJwtGrantedAuthoritiesConverter(new Jwt2AuthoritiesConverter());
        jwtConverter.setPrincipalClaimName("preferred_username");

        return jwtConverter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Enable OAuth2 with custom authorities mapping
        http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());

//        // Enable anonymous
//        http.anonymous();
//
//        // Enable and configure CORS
//        http.cors().configurationSource(corsConfigurationSource());

        // State-less session (state in access-token only)
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Disable CSRF because of state-less session-management
        http.csrf().disable();

//        // Return 401 (unauthorized) instead of 302 (redirect to login) when
//        // authorization is missing or invalid
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
            .antMatchers("/roles").permitAll()
            .antMatchers("/actuator/prometheus/**").permitAll()
            .antMatchers(HttpMethod.GET, "/videos/videos/*").permitAll()
            .antMatchers(HttpMethod.GET, "/service/files/**").permitAll()

            // permission to access all static resources
            .antMatchers("/resources/**").permitAll()
            .antMatchers("/css/**").permitAll()
            .antMatchers("/image/**").permitAll()
            .antMatchers("/js/**").permitAll()
            .antMatchers("/chatSocketHandler/**").permitAll()

            .antMatchers("/export-problem/*")
            .permitAll()
            .antMatchers("/edu/assignment/*/submissions")
            .permitAll()
            .antMatchers("/edu/class/**")
            .hasAnyRole("TEACHER", "STUDENT")
            .antMatchers("/edu/assignment/**")
            .hasAnyRole("TEACHER", "STUDENT")
            .regexMatchers("/v2/api-docs")
            .permitAll()
            .regexMatchers("/.*swagger.*")
            .permitAll()
            .regexMatchers(".*/user/register/*$")
            .permitAll()
            .antMatchers("/public/**")
            .permitAll()
            .anyRequest().authenticated()
            .and()
            .requestCache()
            .requestCache(new NullRequestCache()) // Not cache request because of having frontend
            .and()
            .httpBasic()
//            .authenticationEntryPoint(basicAuthenticationEndPoint)
            .disable()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())
            .and()
            .headers()
            .frameOptions()
            .disable()
//            .and()
//            .logout()
//            .logoutSuccessUrl("/")
        ;
    }

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
