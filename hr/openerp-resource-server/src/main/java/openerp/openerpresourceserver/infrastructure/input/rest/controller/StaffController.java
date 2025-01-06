package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.domain.model.StaffDetailModel;
import openerp.openerpresourceserver.domain.model.StaffModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request.AddStaffRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request.EditStaffRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request.GetAllStaffInfoRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.request.SearchStaffRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.response.StaffDetailResponse;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.staff.response.StaffResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff/")
public class StaffController extends BeanAwareUseCasePublisher {
    @PostMapping("add-staff")
    public ResponseEntity<?> addStaff(
            @Valid @RequestBody AddStaffRequest staff
    ){
        publish(staff.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("edit-staff")
    public ResponseEntity<?> editStaff(
            @Valid @RequestBody EditStaffRequest staff
    ){
        publish(staff.toUseCase());
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("search-staff")
    public ResponseEntity<?> searchStaff(
            @Valid @RequestBody SearchStaffRequest staff
    ){
        var staffPage = publishPageWrapper(StaffModel.class, staff.toUseCase());
        var responsePage = staffPage.convert(StaffResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }

    @PostMapping("get-all-staff-info")
    public ResponseEntity<?> getAllStaffInfo(
            @Valid @RequestBody GetAllStaffInfoRequest staff
    ){
        var staffPage = publishPageWrapper(StaffDetailModel.class, staff.toUseCase());
        var responsePage = staffPage.convert(StaffDetailResponse::fromModel);
        return ResponseEntity.ok().body(
                new Resource(responsePage)
        );
    }
}
