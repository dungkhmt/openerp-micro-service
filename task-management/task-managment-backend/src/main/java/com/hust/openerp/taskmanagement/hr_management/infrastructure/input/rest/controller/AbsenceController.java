package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;


import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.CancelAbsence;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.GetAbsence;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.GetAbsenceList;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.absence.request.AnnounceAbsenceRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.absence.request.UpdateAbsenceRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.absence.response.AbsenceResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/absences")
public class AbsenceController extends BeanAwareUseCasePublisher {

    @PostMapping
    public ResponseEntity<?> announceAbsence(
        Principal principal,
        @Valid @RequestBody AnnounceAbsenceRequest request
    ){
        publish(request.toUseCase(principal.getName()));
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAbsence(
        Principal principal,
        @Valid @PathVariable UUID id,
        @Valid @RequestBody UpdateAbsenceRequest request
    ){
        publish(request.toUseCase(principal.getName(), id));
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelAbsence(
        Principal principal,
        @Valid @PathVariable UUID id
    ){
        var useCase = CancelAbsence.builder()
            .id(id)
            .userId(principal.getName())
            .build();
        publish(useCase);
        return ResponseEntity.ok().body(
            new Resource()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAbsenceList(
        @Valid @PathVariable UUID id
    ){
        var useCase = GetAbsence.builder()
            .id(id)
            .build();
        var absence = publish(AbsenceModel.class, useCase);
        return ResponseEntity.ok().body(
            new Resource(AbsenceResponse.fromModel(absence))
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getAbsenceList(
        @Valid @RequestParam List<String> userIds,
        @Valid @RequestParam LocalDate startDate,
        @Valid @RequestParam LocalDate endDate
    ){
        var useCase = GetAbsenceList.builder()
            .userIds(userIds)
            .startDate(startDate)
            .endDate(endDate)
            .build();
        var absenceList = publishCollection(AbsenceModel.class, useCase);
        return ResponseEntity.ok().body(
            new Resource(AbsenceResponse.fromModels(absenceList))
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAbsenceListOfStaff(
        Principal principal,
        @Valid @RequestParam LocalDate startDate,
        @Valid @RequestParam LocalDate endDate
        ){
        var useCase = GetAbsenceList.builder()
            .userIds(List.of(principal.getName()))
            .startDate(startDate)
            .endDate(endDate)
            .build();
        var absenceList = publishCollection(AbsenceModel.class, useCase);
        return ResponseEntity.ok().body(
            new Resource(AbsenceResponse.fromModels(absenceList))
        );
    }
}
