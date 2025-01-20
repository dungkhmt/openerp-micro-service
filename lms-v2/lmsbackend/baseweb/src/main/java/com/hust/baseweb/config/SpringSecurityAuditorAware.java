package com.hust.baseweb.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {

    public Optional<String> getCurrentAuditor() {
        return Optional
            .ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .map(principal -> {
                if (principal instanceof Jwt) {
                    Jwt jwt = (Jwt) principal;
                    return jwt.getClaim("preferred_username");
                } else if (principal instanceof User) {
                    User user = (User) principal;
                    return user.getUsername();
                } else if (principal instanceof String) {
                    return (String) principal;
                }

                return null;
            });
    }
}
