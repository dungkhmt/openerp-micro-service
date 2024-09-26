package com.example.api.controllers.admin;

import com.example.api.controllers.admin.dto.AddEmployeeRequest;
import com.example.api.controllers.admin.dto.DeleteEmployeeRequest;
import com.example.api.controllers.admin.dto.UpdateEmployeeRequest;
import com.example.api.services.employee.EmployeeService;
import com.example.api.services.employee.dto.ListEmployeeFilterParam;
import com.example.shared.enumeration.EmployeeRole;
import com.example.shared.response.CommonResponse;
import com.example.shared.utils.PageableUtils;
import com.example.shared.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/employee")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/pagination")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getEmployees(ListEmployeeFilterParam filterParam,
                                                               Integer page, Integer size,
                                                               String sort) {
        Pageable pageable = PageableUtils.generate(page, size, sort);

        return ResponseUtil.toSuccessCommonResponse(
                employeeService.getListEmployee(filterParam, pageable)
        );
    }

    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> addEmployee(
        @RequestBody AddEmployeeRequest request
        ) {
        employeeService.addEmployee(request.toInput());
        return ResponseUtil.toSuccessCommonResponse("Add employee successfully");
    }

    @PutMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> updateEmployee(
        @RequestBody UpdateEmployeeRequest request
        ) {
        employeeService.updateEmployee(request.toInput());
        return ResponseUtil.toSuccessCommonResponse("Update employee successfully");
    }

    @DeleteMapping("")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> deleteEmployee(
        @RequestBody DeleteEmployeeRequest request
        ) {
        employeeService.deleteEmployee(request.getId());
        return ResponseUtil.toSuccessCommonResponse("Delete employee successfully");
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<Object>> getAvailableEmployees(
        @RequestParam(required = false) EmployeeRole role,
        @RequestParam(required = false) String query
        ) {
        return ResponseUtil.toSuccessCommonResponse(
            employeeService.getAvailableEmployees(role, query)
        );
    }
}
