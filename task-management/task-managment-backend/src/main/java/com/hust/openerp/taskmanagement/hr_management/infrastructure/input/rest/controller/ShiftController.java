package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data.DeleteShifts;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data.GetShift;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data.GetShiftList;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.shift.request.CreateShiftsRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.shift.request.UpdateShiftRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.shift.response.ShiftResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shifts")
public class ShiftController extends BeanAwareUseCasePublisher {

    @PostMapping
    public ResponseEntity<?> createShiftsRequest(
        @Valid @RequestBody CreateShiftsRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateShift(
        @Valid @RequestBody UpdateShiftRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @DeleteMapping("")
    public ResponseEntity<?> cancelShift(
        @Valid @RequestBody List<UUID> ids
    ){
        var useCase = new DeleteShifts(ids);
        publish(useCase);
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShift(
        @Valid @PathVariable UUID id
    ){
        var useCase = GetShift.builder()
            .id(id)
            .build();
        var shift = publish(ShiftModel.class, useCase);
        return ResponseEntity.ok().body(
            new Resource(ShiftResponse.fromModel(shift))
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getShiftList(
        @Valid @RequestParam List<String> userIds,
        @Valid @RequestParam LocalDate startDate,
        @Valid @RequestParam LocalDate endDate
    ){
        var useCase = GetShiftList.builder()
            .userIds(userIds)
            .startDate(startDate)
            .endDate(endDate)
            .hasUnassigned(true)
            .build();
        var shiftList = publishCollection(ShiftModel.class, useCase);
        return ResponseEntity.ok().body(
            new Resource(ShiftResponse.fromModels(shiftList))
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getShiftListOfStaff(
        Principal principal,
        @Valid @RequestParam LocalDate startDate,
        @Valid @RequestParam LocalDate endDate
        ){
        var useCase = GetShiftList.builder()
            .userIds(List.of(principal.getName()))
            .startDate(startDate)
            .endDate(endDate)
            .hasUnassigned(false)
            .build();
        var shiftList = publishCollection(ShiftModel.class, useCase);
        return ResponseEntity.ok().body(
            new Resource(ShiftResponse.fromModels(shiftList))
        );
    }
}
