package com.example.api.configs.security;

import com.example.api.utils.JwtUtil;
import com.example.shared.db.entities.Account;
import com.example.shared.db.repo.AccountRepository;
import com.example.shared.exception.MyException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException, MyException {
        String token = this.recoverToken(request);
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (token != null) {
            log.info("Token: " + token);
            try {
                Long userId = Long.valueOf(JwtUtil.validateToken(token));
                Account account = accountRepository.findById(userId).orElseThrow(
                    () -> new UsernameNotFoundException("User not found")
                );
                CustomUserDetails userDetails = CustomUserDetails.fromAccount(account);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    account, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (MyException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.replace("Bearer ", "");
        }
        return null;
    }
}
