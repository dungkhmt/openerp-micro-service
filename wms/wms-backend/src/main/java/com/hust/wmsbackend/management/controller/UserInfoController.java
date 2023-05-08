package com.hust.wmsbackend.management.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/wmsv2/user")
@AllArgsConstructor(onConstructor_ = @Autowired)
@CrossOrigin
@Validated
public class UserInfoController {
    @GetMapping
    public ResponseEntity<String> getUserLoginId(Principal principal) {
        return ResponseEntity.ok(principal.getName());
    }
}
