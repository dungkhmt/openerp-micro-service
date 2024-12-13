package openerp.openerpresourceserver.thesisdefensejuryassignment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseSession;
import openerp.openerpresourceserver.thesisdefensejuryassignment.service.DefenseSessionServiceImpl;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/defense-session")
@Log4j2

public class DefenseSessionController {
    private DefenseSessionServiceImpl defenseSessionService;
    @GetMapping("/get-all")
    public ResponseEntity<List<DefenseSession>> getAllRoom(){
        return new ResponseEntity<>(defenseSessionService.getAllDefenseSession(), HttpStatus.OK);
    }
}
