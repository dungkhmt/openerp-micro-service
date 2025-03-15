package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request.CreateCheckpointConfigureRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request.DeleteCheckpointConfigureRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request.GetAllCheckpointConfigureRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.request.UpdateCheckpointConfigureRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.checkpoint_configure.response.CheckpointConfigureResponse;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkpoint/")
public class CheckpointConfigureController extends BeanAwareUseCasePublisher {
    @PostMapping("create-configure")
    public ResponseEntity<?> createCheckpointConfigure(
            @Valid @RequestBody CreateCheckpointConfigureRequest request
    ){
        var model = publish(CheckpointConfigureModel.class ,request.toUseCase());
        var response = CheckpointConfigureResponse.fromModel(model);
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }

    @PostMapping("update-configure")
    public ResponseEntity<?> updateCheckpointConfigure(
            @Valid @RequestBody UpdateCheckpointConfigureRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("delete-configure")
    public ResponseEntity<?> deleteCheckpointConfigure(
            @Valid @RequestBody DeleteCheckpointConfigureRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("get-all-configure")
    public ResponseEntity<?> getAllCheckpointConfigure(
            @Valid @RequestBody GetAllCheckpointConfigureRequest request
    ){
        var modelPage = publishPageWrapper(CheckpointConfigureModel.class, request.toUseCase());
        var responsePage = modelPage.convert(CheckpointConfigureResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }
}
