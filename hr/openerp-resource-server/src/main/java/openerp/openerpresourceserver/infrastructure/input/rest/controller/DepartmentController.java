package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.domain.model.DepartmentModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.department.request.CreateDepartmentRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.department.request.GetDepartmentRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.department.request.UpdateDepartmentRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.department.response.DepartmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/")
public class DepartmentController extends BeanAwareUseCasePublisher {
    @PostMapping("create_department")
    public ResponseEntity<?> createDepartment(
            @RequestBody CreateDepartmentRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("update_department")
    public ResponseEntity<?> updateDepartment(
            @RequestBody UpdateDepartmentRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("get_department")
    public ResponseEntity<?> getDepartment(
            @RequestBody GetDepartmentRequest request
    ){
        var modelPage = publishPageWrapper(DepartmentModel.class, request.toUseCase());
        var responsePage = modelPage.convert(DepartmentResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }
}
