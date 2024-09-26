package com.example.api.services.auth;

import com.example.api.configs.security.CustomUserDetails;
import com.example.api.services.auth.dto.SignUpInput;
import com.example.shared.db.entities.Account;
import com.example.shared.db.repo.AccountRepository;
import com.example.shared.enumeration.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService implements UserDetailsService {
    private final AccountRepository accountRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found")
        );

        return CustomUserDetails.fromAccount(account);
    }

    public Account signUp(SignUpInput input) {
        if (accountRepository.findByUsername(input.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        String encryptedPassword = encodePassword(input.getPassword());
        Account account = Account.builder()
                .username(input.getUsername())
                .password(encryptedPassword)
                .role(input.getRole() == null ? UserRole.CLIENT : input.getRole())
                .build();
        return accountRepository.save(account);
    }

    public String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
