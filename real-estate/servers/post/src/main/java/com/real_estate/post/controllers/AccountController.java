package com.real_estate.post.controllers;

import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.AccountService;
import com.real_estate.post.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping()
    public ResponseEntity<ResponseDto<AccountResponseDto>> getAccount() {
        Long accountId = authenticationService.getAccountIdFromContext();
        AccountResponseDto dto = accountService.getAccountBy(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, dto));
    }
}
