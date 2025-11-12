package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.UpdateAccountRequestDto;
import com.real_estate.post.dtos.request.UpdatePasswordRequestDto;
import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.AccountService;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.GmailSenderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    GmailSenderService gmailSenderService;

    @GetMapping()
    public ResponseEntity<ResponseDto<AccountResponseDto>> getAccount() {
        Long accountId = authenticationService.getAccountIdFromContext();
        AccountResponseDto dto = accountService.getAccountBy(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, dto));
    }

    @PutMapping("")
    public ResponseEntity<ResponseDto<String>> updateInfo(@RequestBody UpdateAccountRequestDto requestDto) {
        Long accountId = authenticationService.getAccountIdFromContext();
        accountService.updateInfo(requestDto, accountId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Cập nhập thông tin thành công"));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<ResponseDto<String>> updatePassword(@RequestBody UpdatePasswordRequestDto requestDto) {
        Long accountId = authenticationService.getAccountIdFromContext();
        accountService.updatePassword(requestDto, accountId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Cập nhập mật khẩu thành công"));
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null){
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            System.out.println("logout");
            request.logout();
            response.sendRedirect("http://localhost:2804/");
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
