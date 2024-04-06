package openerp.openerpresourceserver.controller.general;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.service.GeneralClassOpenedService;

@RestController
@RequestMapping("/general-classes")
public class GeneralClassOpenedController {
    @Autowired
    private GeneralClassOpenedService gService;


    @GetMapping("/")
    public ResponseEntity<List<GeneralClassOpened>> getClasses(@RequestParam String semester) {
        try {
            return ResponseEntity.ok(gService.getGeneralClasses(semester));
        } catch(Exception e) {
            System.err.println(e);
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }
    
    @PostMapping("/update")
    public GeneralClassOpened updateClass() {
        return gService.updateGeneralClass(null);
    }


}
