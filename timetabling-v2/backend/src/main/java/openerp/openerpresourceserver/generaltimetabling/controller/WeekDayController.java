package openerp.openerpresourceserver.generaltimetabling.controller;

import openerp.openerpresourceserver.generaltimetabling.model.entity.WeekDay;
import openerp.openerpresourceserver.generaltimetabling.service.WeekDayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/weekday")
public class WeekDayController {

    @Autowired
    private WeekDayService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<WeekDay>> getAllWeekDay() {
        try {
            List<WeekDay> weekDayList = service.getWeekDay();
            if (weekDayList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(weekDayList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/update")
    public ResponseEntity<Void> updateWeekDay() {
        try {
            service.updateWeekDay();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
