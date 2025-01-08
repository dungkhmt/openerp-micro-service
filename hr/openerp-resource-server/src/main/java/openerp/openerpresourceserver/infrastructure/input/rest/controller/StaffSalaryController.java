package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.domain.model.StaffDepartmentModel;
import openerp.openerpresourceserver.domain.model.StaffSalaryModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.salary.request.GetSalaryRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.salary.request.UpdateSalaryRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.salary.response.StaffSalaryResponse;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff_department.request.GetDepartmentHistoryRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff_department.response.StaffDepartmentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/salary/")
public class StaffSalaryController extends BeanAwareUseCasePublisher {

    @PostMapping("get-salary")
    public ResponseEntity<?> getStaffSalary(
            @Valid @RequestBody GetSalaryRequest request
    ){
        var model = publish(StaffSalaryModel.class, request.toUseCase());
        var response = StaffSalaryResponse.fromModel(model);
        return ResponseEntity.ok().body(
                new Resource(response)
        );
    }

    @PostMapping("update-salary")
    public ResponseEntity<?> updateStaffSalary(
            @Valid @RequestBody UpdateSalaryRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }
}
