package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.model.entity.StudyTime;
import openerp.openerpresourceserver.service.StudyTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/study-time")
public class StudyTimeController {

    @Autowired
    private StudyTimeService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<StudyTime>> getAllStudyTime() {
        try {
            List<StudyTime> studyTimeList = service.getStudyTime();
            if (studyTimeList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(studyTimeList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Void> updateStudyTime() {
        try {
            service.updateStudyTime();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
