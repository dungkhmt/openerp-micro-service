package com.example.api.controllers.auth.dto;

import com.example.api.services.auth.dto.LoginInput;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull
    private String username;
    @NotNull
    private String password;
    public static LoginInput toInput(LoginRequest request) {
        return LoginInput.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }
}
