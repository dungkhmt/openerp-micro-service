package openerp.openerpresourceserver.generaltimetabling.controller.general;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.exception.InvalidFieldException;
import openerp.openerpresourceserver.generaltimetabling.exception.NotFoundException;
import openerp.openerpresourceserver.generaltimetabling.model.dto.MakeGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.UpdatePlanClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;
import openerp.openerpresourceserver.generaltimetabling.service.impl.PlanGeneralClassService;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/plan-general-classes")
@Controller
@AllArgsConstructor
public class PlanGeneralClassController {
    private PlanGeneralClassService planClassService;

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity resolveInvalidFieldException(InvalidFieldException e) {
        return ResponseEntity.status(410).body(e.getErrorMessage());
    }


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity resolveNotFoundException(NotFoundException e) {
        return ResponseEntity.status(410).body(e.getCustomMessage());
    }


    @PostMapping("/make-class")
    public ResponseEntity<GeneralClass> requestMakeClass(@RequestBody MakeGeneralClassRequest request) {
        return ResponseEntity.ok(planClassService.makeClass(request));
    }

    @GetMapping("/")
    public ResponseEntity<List<PlanGeneralClass>> requestGetPlanClasses(@RequestParam("semester") String semester) {
        return ResponseEntity.ok(planClassService.getAllClasses(semester));
    }


    @GetMapping("/view-class")
    public ResponseEntity requestViewPlanClass(@RequestParam("semester") String semester,
                                               @RequestParam("planClassId") Long planClassId){
        return ResponseEntity.ok(planClassService.getPlanClassById(semester, planClassId));
    }

    @PostMapping("/update-general-class")
    public ResponseEntity requestUpdateGeneralClass(@RequestBody UpdateGeneralClassRequest request) {
        return  ResponseEntity.ok(planClassService.updateGeneralClass(request.getGeneralClass()));
    }

    @PostMapping("/update-plan-class")
    public ResponseEntity requestUpdatePlanClass(@RequestBody UpdatePlanClassRequest request) {
        return  ResponseEntity.ok(planClassService.updatePlanClass(request.getPlanClass()));
    }

    @DeleteMapping("/")
    public ResponseEntity<PlanGeneralClass> requestDeletePlanClass(@RequestParam("planClassId") Long planClassId) {
        return ResponseEntity.ok(planClassService.deleteClassById(planClassId));
    }


}
