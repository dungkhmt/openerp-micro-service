package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.model.dto.request.FilterScheduleDto;
import openerp.openerpresourceserver.model.entity.TimePerformance;
import openerp.openerpresourceserver.service.TimePerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/time-performance")
public class TimePerformanceController {

    @Autowired
    private TimePerformanceService timePerformanceService;

    @PostMapping("/search")
    public ResponseEntity<List<TimePerformance>> getTimePerformance(@Valid @RequestBody FilterScheduleDto requestDto) {
        try {
            List<TimePerformance> timePerformanceList = timePerformanceService.getTimePerformance(requestDto);
            if (timePerformanceList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(timePerformanceList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<TimePerformance>> getALl() {
        try {
            List<TimePerformance> timePerformanceList = timePerformanceService.getAll();
            if (timePerformanceList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(timePerformanceList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-by-list")
    public ResponseEntity<Void> deleteByIdList(@RequestParam List<Long> idList) {
        try {
            timePerformanceService.deleteByIdList(idList);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
