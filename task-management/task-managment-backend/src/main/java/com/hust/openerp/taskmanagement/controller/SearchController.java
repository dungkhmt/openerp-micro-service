package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.config.OpenApiConfig;
import com.hust.openerp.taskmanagement.dto.SearchDTO;
import com.hust.openerp.taskmanagement.service.SearchService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "APIs for searching")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/recent-activity")
    public SearchDTO getRecentActivity(Principal principal) {
        return searchService.getRecentActivity(principal.getName());
    }

    @GetMapping
    public SearchDTO search(Principal principal, @RequestParam("q") String q) {
        return searchService.search(principal.getName(), q);
    }
}
