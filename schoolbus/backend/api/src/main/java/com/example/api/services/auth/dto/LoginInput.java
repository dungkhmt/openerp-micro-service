package com.example.api.services.auth.dto;

import com.example.shared.enumeration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginInput {
    private String username;
    private String password;
    private UserRole role;
}
