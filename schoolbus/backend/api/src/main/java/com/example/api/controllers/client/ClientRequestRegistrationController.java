package com.example.api.controllers.client;

import com.example.api.controllers.client.dto.RequestRegistrationCreate;
import com.example.api.services.request_registration.RequestRegistrationService;
import com.example.shared.db.entities.Account;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client/request-registration")
@RequiredArgsConstructor
@Slf4j
public class ClientRequestRegistrationController {
    private final RequestRegistrationService requestRegistrationService;

    @GetMapping("")
    public ResponseEntity<CommonResponse<Object>> getRequestRegistration(
        @AuthenticationPrincipal Account account
    ) {
        return ResponseUtil.toSuccessCommonResponse(
            requestRegistrationService.getListRequestRegistration(account)
        );
    }

    @PostMapping("")
    public ResponseEntity<CommonResponse<Object>> createRequestRegistration(
        @RequestBody RequestRegistrationCreate request,
        @AuthenticationPrincipal Account account
        ) {
        requestRegistrationService.upsertRegistration(request.toInput(), account);
        return ResponseUtil.toSuccessCommonResponse("Request registration successfully");
    }
}
