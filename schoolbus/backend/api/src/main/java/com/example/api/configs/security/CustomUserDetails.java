package com.example.api.configs.security;

import com.example.shared.db.entities.Account;
import com.example.shared.enumeration.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

    private long id;
    private String username;
    private String password;
    private UserRole role;
    private Map<String, Object> attributes;

    public CustomUserDetails (Account account, Map<String, Object> attributes){
        this.id = account.getId();
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.attributes = attributes;
        this.role = account.getRole();
    }
    public static CustomUserDetails fromAccount(Account account) {
        return new CustomUserDetails(
            account.getId(),
            account.getUsername(),
            account.getPassword(),
            account.getRole(),
            null
        );
    }

    public Account toAccount() {
        return Account.builder()
            .id(this.id)
            .username(this.username)
            .password(this.password)
            .role(this.role)
            .build();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ADMIN"),
                           new SimpleGrantedAuthority("CLIENT"));
        } else {
            return List.of(new SimpleGrantedAuthority("CLIENT"));
        }
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return this.username;
    }
}
