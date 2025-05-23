package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RosterStatistics {
    private long totalAssignedShifts;
    private double totalAssignedHours;
    private FairnessStats fairness;
    private List<EmployeeStat> employeeStats;
    private List<String> detailedRosterLog; // For CSV export, contains the detailed log

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FairnessStats {
        private double minEmployeeHours;
        private double maxEmployeeHours;
        private double rangeHours;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeStat {
        private String employeeId;
        private String employeeName;
        private long totalShifts;
        private double totalHours;
        private int nightShifts;
        private int weekendDaysWorked;
        private int maxConsecutiveWorkDays;
    }
}