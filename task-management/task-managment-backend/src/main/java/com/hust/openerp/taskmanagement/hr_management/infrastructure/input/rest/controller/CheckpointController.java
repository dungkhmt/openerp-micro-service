package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_staff.usecase_data.GetCheckpoint;
import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.request.CheckpointStaffRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.request.GetAllCheckpointRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.request.GetCheckpointRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.response.CheckpointResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/checkpoints")
public class CheckpointController extends BeanAwareUseCasePublisher {
    @PostMapping("")
    public ResponseEntity<?> checkpointStaff(
            Principal principal,
            @Valid @RequestBody CheckpointStaffRequest request
    ){
        request.setCheckedByUserId(principal.getName());
        var model = publish(CheckpointModel.class, request.toUseCase());
        var response = CheckpointResponse.fromModel(model);
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCheckpoint(
            @Valid @RequestBody GetAllCheckpointRequest request
    ) {
        var checkpoints = publishCollection(CheckpointModel.class, request.toUseCase());
        var response = checkpoints.stream()
                .map(CheckpointResponse::fromModel)
                .toList();
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }

    @GetMapping("/{periodId}/{userId}")
    public ResponseEntity<?> getCheckpoint(
        @PathVariable("periodId") UUID periodId,
        @PathVariable("userId") String userId
    ) {
        var checkpoint = publish(CheckpointModel.class, GetCheckpoint.builder()
            .periodId(periodId)
            .userId(userId)
            .build());
        var response = CheckpointResponse.fromModel(checkpoint);
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }
}
