package openerp.openerpresourceserver.infrastructure.input.rest.controller;

import openerp.openerpresourceserver.application.port.out.staff.usecase_data.ExampleUseCase;
import openerp.openerpresourceserver.domain.common.usecase.BeanAwareUseCasePublisher;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/v1/")
public class ExampleController extends BeanAwareUseCasePublisher {

    @PostMapping("example")
    public ResponseEntity<?> example(Principal principal){
        publish(new ExampleUseCase());
        return ResponseEntity.ok().body(
                new Resource(null)
        );
    }
}
