package com.example.api.controllers.client;

import com.example.api.controllers.client.dto.StudentRidesFilterParam;
import com.example.api.services.parent_student.ParentStudentService;
import com.example.shared.db.entities.Account;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client/parent-student")
@RequiredArgsConstructor
@Slf4j
public class ClientParentStudentController {
    private final ParentStudentService parentStudentService;

    @GetMapping("/student-rides")
    public ResponseEntity<CommonResponse<Object>> getStudentRides(
        StudentRidesFilterParam param,
        @AuthenticationPrincipal Account account
    ) {
        return ResponseUtil.toSuccessCommonResponse(
            parentStudentService.getStudentRides(account)
        );
    }

}
