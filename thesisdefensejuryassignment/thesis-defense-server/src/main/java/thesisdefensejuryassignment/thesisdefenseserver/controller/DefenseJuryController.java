package thesisdefensejuryassignment.thesisdefenseserver.controller;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseJury;
import thesisdefensejuryassignment.thesisdefenseserver.models.DefenseJuryIM;
import thesisdefensejuryassignment.thesisdefenseserver.service.DefenseJuryServiceImpl;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/defense-jury")
@Log4j2

public class DefenseJuryController {
    private DefenseJuryServiceImpl juryService;
    private static Logger logger = LogManager.getLogger(DefenseJuryController.class);

    @PostMapping("/save")
    public ResponseEntity<DefenseJury> createDefenseJury(
            @RequestBody DefenseJuryIM request
    ){
        DefenseJury createdJury = juryService.createNewDefenseJury(request);
        if (createdJury == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(juryService.createNewDefenseJury(request), HttpStatus.OK);
    }
}
