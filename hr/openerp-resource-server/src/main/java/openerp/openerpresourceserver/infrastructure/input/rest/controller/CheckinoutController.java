package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.Checkinout;
import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.domain.model.AttendanceModel;
import openerp.openerpresourceserver.domain.model.CheckinoutModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkinout.request.GetCheckinoutRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkinout.request.MonthAttendanceRequest;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkinout.response.GetCheckinoutResponse;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.checkinout.response.MonthAttendanceResponse;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/v1/")
public class CheckinoutController extends BeanAwareUseCasePublisher {

    @PostMapping("checkinout")
    public ResponseEntity<?> checkinout(Principal principal){
        publish(Checkinout.from(principal.getName()));
        return ResponseEntity.ok().body(
                new Resource()
        );
    }

    @PostMapping("get_checkinout")
    public ResponseEntity<?> getCheckinout(
            @RequestBody GetCheckinoutRequest request
    ){
        var models = publishCollection(CheckinoutModel.class, request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource(models.stream().map(GetCheckinoutResponse::fromModel).toList())
        );
    }

    @PostMapping("month_attendance")
    public ResponseEntity<?> getMonthAttendance(
            @RequestBody MonthAttendanceRequest request
    ){
        var models = publishCollection(AttendanceModel.class, request.toUseCase());
        return ResponseEntity.ok().body(
                new Resource(MonthAttendanceResponse.fromModels(models))
        );
    }
}
