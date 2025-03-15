package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.request.CheckpointStaffRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.request.GetAllCheckpointRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.request.GetCheckpointRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_staff.response.CheckpointResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/checkpoint/")
public class CheckpointController extends BeanAwareUseCasePublisher {
    @PostMapping("checkpoint-staff")
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

    @PostMapping("get-all-checkpoint")
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

    @PostMapping("get-checkpoint")
    public ResponseEntity<?> getCheckpoint(
            @Valid @RequestBody GetCheckpointRequest request
    ) {
        var checkpoint = publish(CheckpointModel.class, request.toUseCase());
        var response = CheckpointResponse.fromModel(checkpoint);
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }
}
