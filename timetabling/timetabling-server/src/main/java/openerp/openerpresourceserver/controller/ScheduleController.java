package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.dto.request.RequestPerformanceDto;
import openerp.openerpresourceserver.model.entity.*;
import openerp.openerpresourceserver.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/search")
    public ResponseEntity<List<Schedule>> getScheduleByCondition(@Valid @RequestBody FilterScheduleDto requestDto) {
        try {
            List<Schedule> result = scheduleService.searchSchedule(requestDto);
            if (result.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/calculate-time")
    public ResponseEntity<Void> calculateTimePerformance(@Valid @RequestBody RequestPerformanceDto requestDto) {
        try {
            scheduleService.calculateTimePerformance(requestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/calculate-time-all")
    public ResponseEntity<Void> calculateAllTimePerformance() {
        try {
            scheduleService.calculateAllTimePerformance();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
