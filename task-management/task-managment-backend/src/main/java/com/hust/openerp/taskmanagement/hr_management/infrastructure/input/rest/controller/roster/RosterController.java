package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roster")
public class RosterController {

    private final RosterService rosterService;

    @Autowired
    public RosterController(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @PostMapping("/generate")
    public ResponseEntity<List<ScheduledShift>> generateRoster(@RequestBody RosterRequest rosterRequest) {
        try {
            List<ScheduledShift> schedule = rosterService.generateSchedule(rosterRequest);
            if (schedule.isEmpty() && !rosterService.wasFeasible()) { // Thêm một cờ để biết có tìm được giải pháp không
                return ResponseEntity.status(422).body(null); // Unprocessable Entity - không tìm thấy lịch phù hợp
            }
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            // Log lỗi chi tiết
            System.err.println("Error generating roster: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}