package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roster")
public class RosterController {

    private final RosterService rosterService;

    @Autowired
    public RosterController(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @PostMapping("/generate")
    public ResponseEntity<RosterSolution> generateRoster(@RequestBody RosterRequest rosterRequest) {
        try {
            RosterSolution solution = rosterService.generateSchedule(rosterRequest);
            if ((solution.getScheduledShifts() == null || solution.getScheduledShifts().isEmpty()) && !rosterService.wasFeasible()) {
                return ResponseEntity.status(422).body(solution);
            }
            return ResponseEntity.ok(solution);
        } catch (Exception e) {
            System.err.println("Error generating roster: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                RosterSolution.builder()
                    .statistics(RosterStatistics.builder().detailedRosterLog(List.of("Server error: " + e.getMessage())).build())
                    .build()
            );
        }
    }
}