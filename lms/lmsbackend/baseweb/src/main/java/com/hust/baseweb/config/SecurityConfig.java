package com.hust.baseweb.config;

import com.hust.baseweb.applications.education.exception.CustomAccessDeniedHandler;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.BaseWebUserDetailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@AllArgsConstructor(onConstructor_ = @Autowired)
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private BaseWebUserDetailService userDetailsService;
    private BasicAuthenticationEndPoint basicAuthenticationEndPoint;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(this.userDetailsService).passwordEncoder(UserLogin.PASSWORD_ENCODER);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            .authorizeRequests()
            .antMatchers("/roles").permitAll()
            .antMatchers("/actuator/prometheus/**").permitAll()
            .antMatchers(HttpMethod.GET, "/videos/videos/*").permitAll()

            //permission to access all static resources
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
            .hasAnyRole("EDUCATION_TEACHING_MANAGEMENT_TEACHER", "EDUCATION_LEARNING_MANAGEMENT_STUDENT")
            .antMatchers("/edu/assignment/**")
            .hasAnyRole("EDUCATION_TEACHING_MANAGEMENT_TEACHER", "EDUCATION_LEARNING_MANAGEMENT_STUDENT")
            .regexMatchers("/v2/api-docs")
            .permitAll()
            .regexMatchers("/.*swagger.*")
            .permitAll()
            .regexMatchers(".*/user/register/*$")
            .permitAll()
            .antMatchers("/public/**")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .requestCache()
            .requestCache(new NullRequestCache())
            .and() // Not cache request because of having frontend
            .httpBasic()
            .authenticationEntryPoint(basicAuthenticationEndPoint)
            .and()
            .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())
            .and()
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
            .and()
            .logout()
            .logoutSuccessUrl("/");

    }

    @Bean
    public CustomAccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean corsFilterRegistrationBean() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
