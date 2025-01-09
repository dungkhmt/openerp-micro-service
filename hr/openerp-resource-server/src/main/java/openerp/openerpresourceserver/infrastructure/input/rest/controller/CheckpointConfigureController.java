package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_configure.request.CreateCheckpointConfigureRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_configure.request.DeleteCheckpointConfigureRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_configure.request.GetAllCheckpointConfigureRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_configure.request.UpdateCheckpointConfigureRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_configure.response.CheckpointConfigureResponse;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
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
