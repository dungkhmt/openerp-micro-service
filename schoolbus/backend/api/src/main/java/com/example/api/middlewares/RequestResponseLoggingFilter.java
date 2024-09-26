package com.example.api.middlewares;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@Order(HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String fullUri =
                request.getRequestURI() + (StringUtils.isEmpty(request.getQueryString()) ? "" :
                        "?" + request.getQueryString());
        log.info("start request {} - [{}]", request.getMethod(), fullUri);
        filterChain.doFilter(request, response);
        log.info("done request [{}] with response status {}", fullUri,
                response.getStatus());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "/".equals(request.getRequestURI()) || request.getRequestURI().contains("actuator");
    }

}