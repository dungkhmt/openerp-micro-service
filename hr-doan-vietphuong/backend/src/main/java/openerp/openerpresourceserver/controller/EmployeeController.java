package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.employee.EmployeeQueryRequest;
import openerp.openerpresourceserver.dto.request.employee.EmployeeRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.dto.response.employee.EmployeeResponse;
import openerp.openerpresourceserver.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public Result getEmployeeInfo() {
        return Result.ok(employeeService.getEmployeeInfo());
    }

    @GetMapping("/all")
    public Result getUsers(EmployeeQueryRequest dto, PagingRequest pagingRequest) {
        Page<EmployeeResponse> page = employeeService.getEmployeesByProperties(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @PutMapping
    public Result updateProfile(@RequestBody EmployeeRequest dto) {
        return Result.ok(employeeService.updateProfile(dto));
    }
}
