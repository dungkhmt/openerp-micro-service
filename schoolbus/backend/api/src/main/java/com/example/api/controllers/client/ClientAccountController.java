package com.example.api.controllers.client;

import com.example.api.controllers.admin.dto.ParentUpdateRequest;
import com.example.api.controllers.admin.dto.StudentAddRequest;
import com.example.api.controllers.client.dto.ClientParentAddRequest;
import com.example.api.controllers.client.dto.ClientStudentFilterParam;
import com.example.api.controllers.client.dto.ClientStudentUpdateRequest;
import com.example.api.services.account.AccountService;
import com.example.api.services.account.dto.StudentSearchInput;
import com.example.shared.db.entities.Account;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client/account")
@RequiredArgsConstructor
@Slf4j
public class ClientAccountController {

    private final AccountService accountService;

    @GetMapping("/student/pagination")
    public ResponseEntity<CommonResponse<Object>> getStudents(
         ClientStudentFilterParam request,
        @AuthenticationPrincipal Account account
    ) {
        return ResponseUtil.toSuccessCommonResponse(
            accountService.searchStudents(request.toInput(), account));
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<CommonResponse<Object>> getStudentDetail(
        @PathVariable(name = "id") Long id,
        @AuthenticationPrincipal Account account
    ) {
        return ResponseUtil.toSuccessCommonResponse(accountService.getStudentDetail(id, account));
    }

    @PostMapping("/student")
    public ResponseEntity<CommonResponse<Object>> createStudent(
        @RequestBody StudentAddRequest request,
        @AuthenticationPrincipal Account account) {

        accountService.addStudent(request.toInput(), account);
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @PutMapping("/student/{id}")
    public ResponseEntity<CommonResponse<Object>> updateStudent(
        @RequestBody ClientStudentUpdateRequest request,
        @PathVariable(name = "id") Long id,
        @AuthenticationPrincipal Account account) {
        request.setId(id);
        accountService.updateStudent(request.toInput(), account);
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @DeleteMapping("/student/{id}")
    public ResponseEntity<CommonResponse<Object>> deleteStudent(
        @PathVariable(name = "id") Long id,
        @AuthenticationPrincipal Account account) {
        accountService.deleteStudent(id, account);
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @PostMapping("/parent")
    public ResponseEntity<CommonResponse<Object>> createParent(
        @RequestBody ClientParentAddRequest request) {
        accountService.addParent(request.toInput());
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @PutMapping("/parent/{id}")
    public ResponseEntity<CommonResponse<Object>> updateParent(
        @RequestBody ParentUpdateRequest request,
        @PathVariable(name = "id")
        Long id,
        @AuthenticationPrincipal Account account) {
        request.setId(id);
        accountService.updateParent(request.toInput(), account);
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @DeleteMapping("/parent/{id}")
    public ResponseEntity<CommonResponse<Object>> deleteParent(@PathVariable(name = "id") Long id,
                                                               @AuthenticationPrincipal
                                                               Account account) {
        accountService.deleteParent(id, account);
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @GetMapping("/parent")
    public ResponseEntity<CommonResponse<Object>> getParentDetail(
        @AuthenticationPrincipal Account account
    ) {
        return ResponseUtil.toSuccessCommonResponse(
            accountService.getParentDetail(account));
    }

}
