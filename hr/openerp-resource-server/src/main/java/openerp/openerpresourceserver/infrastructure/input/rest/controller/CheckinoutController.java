package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import openerp.openerpresourceserver.application.port.out.checkinout.usecase_data.Checkinout;
import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.response.resource.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/v1/")
public class CheckinoutController extends BeanAwareUseCasePublisher {
    //@PreAuthorize("hasAnyAuthority('ADMIN')")

    @PostMapping("checkinout")
    public ResponseEntity<?> checkinout(Principal principal){
        publish(Checkinout.from(principal.getName()));
        return ResponseEntity.ok().body(
                new Resource(null)
        );
    }
}
