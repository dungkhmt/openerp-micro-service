package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data.UpdateCheckpointConfigure;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request.CreateCheckpointConfigureRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request.GetAllCheckpointConfigureRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request.UpdateCheckpointConfigureRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.response.CheckpointConfigureResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkpoints/configures")
public class CheckpointConfigureController extends BeanAwareUseCasePublisher {
    @PostMapping("")
    public ResponseEntity<?> createCheckpointConfigure(
        @Valid @RequestBody CreateCheckpointConfigureRequest request
    ){
        var model = publish(CheckpointConfigureModel.class ,request.toUseCase());
        var response = CheckpointConfigureResponse.fromModel(model);
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> updateCheckpointConfigure(
        @PathVariable String code,
        @Valid @RequestBody UpdateCheckpointConfigureRequest request
    ){
        publish(request.toUseCase(code));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteCheckpointConfigure(
        @PathVariable String code
    ){
        publish(UpdateCheckpointConfigure.delete(code));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCheckpointConfigure(
            @Valid @ModelAttribute GetAllCheckpointConfigureRequest request
    ){
        var modelPage = publishPageWrapper(CheckpointConfigureModel.class, request.toUseCase());
        var responsePage = modelPage.convert(CheckpointConfigureResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }
}
