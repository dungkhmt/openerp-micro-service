package com.example.shared.config;

import com.google.common.base.CaseFormat;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SnakeCaseConfig {

    @Bean
    public OncePerRequestFilter snakeCaseConverterFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
                final Map<String, String[]> parameters = new ConcurrentHashMap<>();

                for (String param : request.getParameterMap().keySet()) {
                    String camelCaseParam =
                        CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, param);

                    parameters.put(camelCaseParam, request.getParameterValues(param));
                    parameters.put(param, request.getParameterValues(param));
                }

                filterChain.doFilter(new HttpServletRequestWrapper(request) {
                    @Override
                    public String getParameter(String name) {
                        return parameters.containsKey(name) ? parameters.get(name)[0] : null;
                    }

                    @Override
                    public Enumeration<String> getParameterNames() {
                        return Collections.enumeration(parameters.keySet());
                    }

                    @Override
                    public String[] getParameterValues(String name) {
                        return parameters.get(name);
                    }

                    @Override
                    public Map<String, String[]> getParameterMap() {
                        return parameters;
                    }
                }, response);
            }
        };
    }

}