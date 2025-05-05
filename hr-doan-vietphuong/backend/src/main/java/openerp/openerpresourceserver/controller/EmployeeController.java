package openerp.openerpresourceserver.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.employee.EmployeeRequest;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.service.EmployeeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public Result getAllEmployees() {
        return Result.ok(employeeService.getAllEmployees());
    }

    @PutMapping
    public Result updateProfile(@RequestBody EmployeeRequest dto) {
        return Result.ok(employeeService.updateProfile(dto));
    }

    @PostMapping
    public Result updateAvatar(@RequestParam("image")MultipartFile image) throws IOException {
        return Result.ok(employeeService.updateAvatar(image));
    }
}
