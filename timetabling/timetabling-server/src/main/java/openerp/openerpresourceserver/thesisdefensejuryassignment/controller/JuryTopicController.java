package openerp.openerpresourceserver.thesisdefensejuryassignment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.AcademicKeyword;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.JuryTopic;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Thesis;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.JuryTopicIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisModel;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.UpdateJuryTopicIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.service.AcademicKeywordServiceImpl;
import openerp.openerpresourceserver.thesisdefensejuryassignment.service.JuryTopicServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/jury-topic")
@Log4j2
public class JuryTopicController {
    @Autowired
    private JuryTopicServiceImpl juryTopicService;

    @GetMapping("/get-all")
    public ResponseEntity<List<JuryTopic>> getJuryTopicList() {
        List<JuryTopic> res = juryTopicService.getAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{thesisDefensePlanId}")
    public ResponseEntity<List<JuryTopic>> getJuryTopicById(@PathVariable String thesisDefensePlanId) {
        List<JuryTopic> res = juryTopicService.getAllByThesisDefensePlanId(thesisDefensePlanId);
        if (res == null){
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<String> createNewJuryTopic(@RequestBody JuryTopicIM juryTopicIM) {
        String mes = juryTopicService.createJuryTopic(juryTopicIM);
        if (mes.equals("ERROR")) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(mes, HttpStatus.CREATED);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<String> updateJuryTopic(@PathVariable int juryTopicId, @RequestBody UpdateJuryTopicIM juryTopicIM) {
        String mes = juryTopicService.updateJuryTopic(juryTopicId, juryTopicIM);
        if (mes.equals("ERROR")) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(mes, HttpStatus.CREATED);
    }
}
