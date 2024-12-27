package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.domain.model.StaffModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request.AddStaffRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request.EditStaffRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request.SearchStaffRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.response.StaffResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/")
public class StaffController extends BeanAwareUseCasePublisher {
    @PostMapping("add_staff")
    public ResponseEntity<?> addStaff(
            @RequestBody AddStaffRequest staff
    ){
        publish(staff.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("edit_staff")
    public ResponseEntity<?> editStaff(
            @RequestBody EditStaffRequest staff
    ){
        publish(staff.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("search_staff")
    public ResponseEntity<?> searchStaff(
            @RequestBody SearchStaffRequest staff
    ){
        var staffPage = publishPageWrapper(StaffModel.class, staff.toUseCase());
        var responsePage = staffPage.convert(StaffResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }
}
