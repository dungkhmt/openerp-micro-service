package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.PagingRequest;
import openerp.openerpresourceserver.dto.request.employee.EmployeeQueryRequest;
import openerp.openerpresourceserver.dto.request.employee.EmployeeRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.dto.response.ResultMeta;
import openerp.openerpresourceserver.dto.response.employee.EmployeeResponse;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/employees")
@Validated
public class AdminEmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public Result getEmployeeByProperties(EmployeeQueryRequest dto, PagingRequest pagingRequest) {
        Page<EmployeeResponse> page = employeeService.getEmployeesByProperties(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @GetMapping("/{id}")
    public Result getEmployeeById(@PathVariable Long id) throws NotFoundException {
        return Result.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    public Result addEmployee(@RequestBody @Valid EmployeeRequest dto) throws BadRequestException {
        return Result.ok(employeeService.addEmployee(dto));
    }

    @PutMapping
    public Result updateEmployee(@RequestBody @Valid EmployeeRequest dto) throws BadRequestException, NotFoundException {
        return Result.ok(employeeService.updateEmployee(dto));
    }

    @DeleteMapping
    public Result deleteEmployee(@RequestBody List<Long> idList) throws NotFoundException {
        return Result.ok(employeeService.deleteEmployee(idList));
    }
}
