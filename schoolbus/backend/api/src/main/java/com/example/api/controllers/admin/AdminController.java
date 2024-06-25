package com.example.api.controllers.admin;

import com.example.api.controllers.admin.dto.ExampleARequest;
import com.example.api.services.serviceA.ExampleServiceA;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final ExampleServiceA exampleServiceA;

    @GetMapping("/example")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getExample(
        ExampleARequest request
    ) {
        log.info("---------Hello method started---------");
        Pageable pageable = PageableUtils.generate(request.getPage(), request.getSize(), "");
        try {
            throw new Exception("Test error");
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return ResponseUtil.toSuccessCommonResponse(
                exampleServiceA.getExamplePage(request.toInput(), pageable)
        );
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/example")
    public ResponseEntity<CommonResponse<Object>> postExample(
    ) {
        log.info("This is post /example");
        return ResponseUtil.toInternalErrorCommonResponse();
    }

    @PostMapping("/testKafka")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> testKafka(
    ) {
        log.info("This is testKafka");
        return ResponseUtil.toSuccessCommonResponse(
                exampleServiceA.testKafka()
        );
    }
}
