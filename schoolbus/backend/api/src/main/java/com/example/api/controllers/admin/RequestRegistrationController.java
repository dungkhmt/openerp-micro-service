package com.example.api.controllers.admin;

import com.example.api.controllers.admin.dto.HandleRequestRegistrationRequest;
import com.example.api.controllers.admin.dto.RequestRegistrationFilterParam;
import com.example.api.services.request_registration.RequestRegistrationService;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.PageableUtils;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/request-registration")
@RequiredArgsConstructor
@Slf4j
public class RequestRegistrationController {
    private final RequestRegistrationService requestRegistrationService;

    @GetMapping("/pagination")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getRequestRegistration(
        RequestRegistrationFilterParam filterParam,
        Integer page, Integer size, String sort
    ) {
        Pageable pageable = PageableUtils.generate(page, size, sort);
        return ResponseUtil.toSuccessCommonResponse(
            requestRegistrationService.getPageRequestRegistration(
                filterParam.getStudentName(),
                filterParam.getParentName(),
                filterParam.getStatuses(),
                filterParam.getAddress(),
                pageable
            )
        );
    }

    @PostMapping("/handle")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> handleRequestRegistration(
        @RequestBody HandleRequestRegistrationRequest request
        ) {
        requestRegistrationService.handleRequestRegistration(request.toInput());

        return ResponseUtil.toSuccessCommonResponse(
            "Handle request registration successfully"
        );
    }
}
