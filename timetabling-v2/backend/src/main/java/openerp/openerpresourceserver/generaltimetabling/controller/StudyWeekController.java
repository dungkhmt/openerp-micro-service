package openerp.openerpresourceserver.generaltimetabling.controller;

import openerp.openerpresourceserver.generaltimetabling.model.entity.StudyWeek;
import openerp.openerpresourceserver.generaltimetabling.service.StudyWeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/study-week")
public class StudyWeekController {

    @Autowired
    private StudyWeekService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<StudyWeek>> getAllStudyWeek() {
        try {
            List<StudyWeek> studyWeekList = service.getStudyWeek();
            if (studyWeekList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(studyWeekList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Void> updateStudyWeek() {
        try {
            service.updateStudyWeek();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
