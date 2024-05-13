package openerp.openerpresourceserver.generaltimetabling.controller.general;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.dto.MakeGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.PlanGeneralClass;
import openerp.openerpresourceserver.generaltimetabling.service.impl.PlanGeneralClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/plan-general-classes")
@Controller
@AllArgsConstructor
public class PlanGeneralClassController {
    private PlanGeneralClassService planClassService;
    @PostMapping("/make-class")
    public ResponseEntity<GeneralClass> requestMakeClass(@RequestBody MakeGeneralClassRequest request) {
        return ResponseEntity.ok(planClassService.makeClass(request));
    }

    @GetMapping("/")
    public ResponseEntity<List<PlanGeneralClass>> requestGetPlanClasses(@RequestParam("semester") String semester) {
        return ResponseEntity.ok(planClassService.getAllClasses(semester));
    }

    @PostMapping("/delete-plan")
    public ResponseEntity<String> requestDeletePlan(@RequestParam("semester") String semester) {
        planClassService.deleteAllClasses(semester);
        return ResponseEntity.ok("Đã xóa danh sách kế hoạch lớp!");
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




}
