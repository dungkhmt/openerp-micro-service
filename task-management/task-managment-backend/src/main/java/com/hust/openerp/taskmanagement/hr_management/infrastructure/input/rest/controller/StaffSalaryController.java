package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.GetCurrentStaffSalary;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffSalaryModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.salary.request.GetSalaryListRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.salary.request.UpdateSalaryRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.salary.response.StaffSalaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salaries")
public class StaffSalaryController extends BeanAwareUseCasePublisher {

    @GetMapping("/{userId}")
    public ResponseEntity<?> getStaffSalary(
        @PathVariable String userId
    ){
        var model = publish(StaffSalaryModel.class, GetCurrentStaffSalary.builder()
            .userLoginId(userId)
            .build());
        var response = StaffSalaryResponse.fromModel(model);
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getSalaryList(
        @Valid @ModelAttribute GetSalaryListRequest request
    ){
        var list = publishCollection(StaffSalaryModel.class, request.toUseCase());
        return ResponseEntity.ok().body(
            new Resource(list.stream().map(StaffSalaryResponse::fromModel).toList())
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateStaffSalary(
        @PathVariable String userId,
            @Valid @RequestBody UpdateSalaryRequest request
    ){
        publish(request.toUseCase(userId));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }
}
