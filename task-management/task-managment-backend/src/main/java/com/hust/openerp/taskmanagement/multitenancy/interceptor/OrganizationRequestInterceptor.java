package com.hust.openerp.taskmanagement.multitenancy.interceptor;

import com.hust.openerp.taskmanagement.multitenancy.resolver.OrganizationResolver;
import com.hust.openerp.taskmanagement.multitenancy.util.OrganizationContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class OrganizationRequestInterceptor implements HandlerInterceptor {

    private final OrganizationResolver organizationResolver;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String organizationCode = organizationResolver.resolveOrganizationCode();
        OrganizationContext.setCurrentOrganizationCode(organizationCode);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        OrganizationContext.clear();
    }
}
