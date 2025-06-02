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
import openerp.openerpresourceserver.service.impl.FaceRecognitionService;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/employees")
@Validated
public class AdminEmployeeController {
    private final EmployeeService employeeService;
    private final FaceRecognitionService faceRecognitionService;

    @GetMapping
    public Result getEmployeeByProperties(EmployeeQueryRequest dto, PagingRequest pagingRequest) {
        Page<EmployeeResponse> page = employeeService.getEmployeesByProperties(dto, pagingRequest);
        return Result.ok(page.getContent(), ResultMeta.of(page));
    }

    @GetMapping("/{id}")
    public Result getEmployeeById(@PathVariable Long id) throws NotFoundException {
        return Result.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/{id}/faces")
    public Result getEmployeeFacesById(@PathVariable Long id) throws NotFoundException {
        return Result.ok(employeeService.getEmployeeFacesById(id));
    }

    @PostMapping
    public Result addEmployee(@RequestBody @Valid EmployeeRequest dto) throws BadRequestException {
        return Result.ok(employeeService.addEmployee(dto));
    }

    @PostMapping("/{id}/add-faces")
    public Result detectFaces(@RequestParam("files") MultipartFile[] files,
                              @PathVariable Long id) throws IOException {
        List<String> faces = faceRecognitionService.addFaces(List.of(files), id);
        return Result.ok(faces);
    }
    
    @PutMapping
    public Result updateEmployee(@RequestBody @Valid EmployeeRequest dto) throws BadRequestException, NotFoundException {
        return Result.ok(employeeService.updateEmployee(dto));
    }

    @DeleteMapping
    public Result deleteEmployee(@RequestBody List<Long> idList) throws NotFoundException {
        return Result.ok(employeeService.deleteEmployee(idList));
    }

    @DeleteMapping("delete-faces")
    public Result deleteEmployeeFaces(@RequestParam("filename") String filename) {
        return Result.ok(employeeService.deleteFaces(filename));
    }
}
