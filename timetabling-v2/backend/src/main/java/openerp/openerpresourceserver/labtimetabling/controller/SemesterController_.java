package openerp.openerpresourceserver.labtimetabling.controller;


import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/lab-timetabling/semester")
public class SemesterController_ {
    private SemesterService semesterService;
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(semesterService.getAll());
    }
}
