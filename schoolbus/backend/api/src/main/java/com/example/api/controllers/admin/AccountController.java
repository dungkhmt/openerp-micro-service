package com.example.api.controllers.admin;

import com.example.api.controllers.admin.dto.ParentAddRequest;
import com.example.api.controllers.admin.dto.ParentFilterParam;
import com.example.api.controllers.admin.dto.ParentUpdateRequest;
import com.example.api.controllers.admin.dto.StudentAddRequest;
import com.example.api.controllers.admin.dto.StudentFilterParam;
import com.example.api.controllers.admin.dto.StudentUpdateRequest;
import com.example.api.services.account.AccountService;
import com.example.shared.db.entities.Account;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/admin/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/parent/pagination")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getParents(ParentFilterParam filterParam) {
        var res = accountService.searchParents(filterParam.toInput());
        return ResponseUtil.toSuccessCommonResponse(res);
    }

    @GetMapping("/student/pagination")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getStudents(StudentFilterParam request) {
        var res = accountService.searchStudents(request.toInput());
        return ResponseUtil.toSuccessCommonResponse(res);
    }

    @GetMapping("/student/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getStudentDetail(@PathVariable(name = "id") Long id) {
        return ResponseUtil.toSuccessCommonResponse(accountService.getStudentDetail(id));
    }

    @PostMapping("/student")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> createStudent(
        @RequestBody StudentAddRequest request,
        @AuthenticationPrincipal Account account) {
        accountService.addStudent(request.toInput());
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @PutMapping("/student/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> updateStudent(
        @RequestBody StudentUpdateRequest request,
        @PathVariable(name = "id")
        Long id) {
        request.setId(id);
            accountService.updateStudent(request.toInput());
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @DeleteMapping("/student/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> deleteStudent(
        @PathVariable(name = "id") Long id) {
        accountService.deleteStudent(id);
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @PostMapping("/parent")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> createParent(
        @RequestBody ParentAddRequest request) {
        accountService.addParent(request.toInput());
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @PutMapping("/parent/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> updateParent(
        @RequestBody ParentUpdateRequest request,
        @PathVariable(name = "id")
        Long id) {
        request.setId(id);
        accountService.updateParent(request.toInput());
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @DeleteMapping("/parent/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> deleteParent(@PathVariable(name = "id") Long id) {
        accountService.deleteParent(id);
        return ResponseUtil.toSuccessCommonResponse(null);
    }

    @GetMapping("/parent/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getParentDetail(@PathVariable(name = "id") Long id) {
        return ResponseUtil.toSuccessCommonResponse(accountService.getParentDetail(id));
    }



}
