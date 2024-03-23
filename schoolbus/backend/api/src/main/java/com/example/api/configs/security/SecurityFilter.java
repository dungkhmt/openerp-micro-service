package com.example.api.configs.security;

import com.example.api.utils.JwtUtil;
import com.example.shared.db.entities.Account;
import com.example.shared.db.repo.AccountRepository;
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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = this.recoverToken(request);
        if (token != null) {
            log.info("Token: " + token);
            Long userId = Long.valueOf(JwtUtil.validateToken(token));
            Account account = accountRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
            );
            CustomUserDetails userDetails = CustomUserDetails.fromAccount(account);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                account.getUsername(), null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
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
