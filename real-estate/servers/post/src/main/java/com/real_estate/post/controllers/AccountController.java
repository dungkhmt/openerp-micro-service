package com.real_estate.post.controllers;

import com.real_estate.post.dtos.request.UpdateAccountRequestDto;
import com.real_estate.post.dtos.request.UpdatePasswordRequestDto;
import com.real_estate.post.dtos.response.AccountResponseDto;
import com.real_estate.post.dtos.response.ResponseDto;
import com.real_estate.post.services.AccountService;
import com.real_estate.post.services.AuthenticationService;
import com.real_estate.post.services.GmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Cập nhật thông tin thành công"));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<ResponseDto<String>> updatePassword(@RequestBody UpdatePasswordRequestDto requestDto) {
        Long accountId = authenticationService.getAccountIdFromContext();
        accountService.updatePassword(requestDto, accountId);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(200, "Cập nhật mật khẩu thành công"));
    }
}
