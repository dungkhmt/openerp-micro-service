package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warm-up")
public class WarmUpController {
    @GetMapping
    public ResponseEntity<?> warmUp() {
        return ResponseEntity.ok().body("Warmed up successfully");
    }
}
