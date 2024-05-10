package openerp.openerpresourceserver.generaltimetabling.controller.general;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.dto.MakeClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.service.impl.PlanGeneralClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class PlanGeneralClassController {
    private PlanGeneralClassService planClassService;
    @PostMapping("/make-class")
    public ResponseEntity<GeneralClass> requestMakeClass(@RequestBody MakeClassRequest request) {
        return ResponseEntity.ok(planClassService.makeClass(request));
    }

}
