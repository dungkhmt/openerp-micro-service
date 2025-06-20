package com.hust.openerp.taskmanagement.config;


import com.hust.openerp.taskmanagement.multitenancy.interceptor.OrganizationRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final OrganizationRequestInterceptor organizationRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(organizationRequestInterceptor);
    }
}
