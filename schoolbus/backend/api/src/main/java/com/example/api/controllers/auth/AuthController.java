package com.example.api.controllers.auth;

import com.example.api.configs.security.CustomUserDetails;
import com.example.api.controllers.auth.dto.LoginRequest;
import com.example.api.controllers.auth.dto.SignUpRequest;
import com.example.api.services.auth.AuthService;
import com.example.api.services.auth.dto.SignUpOutput;
import com.example.api.utils.JwtUtil;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Object>> signUp(
        @RequestBody @Validated SignUpRequest request
    ) {
        authService.signUp(request.toInput());
        return ResponseUtil.toSuccessCommonResponse(
            new SignUpOutput(HttpStatus.CREATED)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Object>> login(
        @RequestBody @Validated LoginRequest request
    ) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        );
        Authentication authUser = authenticationManager.authenticate(authenticationToken);
        String accessToken = JwtUtil.generateAccessToken(((CustomUserDetails) authUser.getPrincipal()).toAccount());
        return ResponseUtil.toSuccessCommonResponse(
            accessToken
        );
    }
}
