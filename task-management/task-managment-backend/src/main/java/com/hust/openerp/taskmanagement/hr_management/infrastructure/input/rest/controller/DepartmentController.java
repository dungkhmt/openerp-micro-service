package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.usecase_data.UpdateDepartment;
import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.DepartmentModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request.CreateDepartmentRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request.DeleteDepartmentRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request.GetDepartmentRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.request.UpdateDepartmentRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.department.response.DepartmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/departments")
public class DepartmentController extends BeanAwareUseCasePublisher {
    @PostMapping("")
    public ResponseEntity<?> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> updateDepartment(
        @PathVariable String code,
            @Valid @RequestBody UpdateDepartmentRequest request
    ){
        publish(request.toUseCase(code));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteDepartment(
        @PathVariable String code
    ){
        publish(UpdateDepartment.delete(code));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getDepartments(
        @Valid @ModelAttribute GetDepartmentRequest request
    ){
        var modelPage = publishPageWrapper(DepartmentModel.class, request.toUseCase());
        var responsePage = modelPage.convert(DepartmentResponse::fromModel);
        return ResponseEntity.ok().body(
            new Resource(responsePage)
        );
    }
}
