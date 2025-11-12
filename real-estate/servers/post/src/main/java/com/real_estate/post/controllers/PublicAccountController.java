package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.CreateAccountRequestDto;
import com.real_estate.post.dtos.request.LoginRequest;
import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.security.TokenProvider;
import com.real_estate.post.security.UserPrincipal;
import com.real_estate.post.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/public/account")
public class PublicAccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    TokenProvider tokenProvider;


    @PostMapping("/signup")
    @Operation(summary = "Register a account", operationId = "account.register")
    public ResponseEntity<ResponseDto<String>> register(@RequestBody CreateAccountRequestDto requestDto) {
        accountService.register(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Link xác nhận được gửi đến gmail"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login account", operationId = "account.login")
    public ResponseEntity<ResponseDto<String>> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long accountId = userPrincipal.getId();
        String token = tokenProvider.createToken(accountId);

        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, token));
    }

    @GetMapping("")
    @Operation(summary = "Get info account", operationId = "publicAccount.getInfo")
    public ResponseEntity<ResponseDto<AccountResponseDto>> getInfoAccount(
            @RequestParam("accountId") Long accountId
    ) {
        AccountResponseDto dto = accountService.getAccountBy(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, dto));
    }

    @GetMapping("/forgot-password")
    @Operation(summary = "Forgot Password", operationId = "publicAccount.pass")
    public ResponseEntity<ResponseDto<String>> forgotPass(
            @RequestParam("email") String email
    ) {
        accountService.resetPassword(email);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Mật khẩu mới đã được gửi tới email"));
    }

    @GetMapping("/active")
    @Operation(summary = "active account", operationId = "publicAccount.active")
    public void activeAccount(
            @RequestParam("token") String token,
            HttpServletResponse response
    ) {
        try {
            Long accountId = Long.valueOf(tokenProvider.getAccountIdFromJwt(token));
            accountService.activeAccount(accountId);
            response.sendRedirect("http://localhost:2804/oauth/redirect?token=" + token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
