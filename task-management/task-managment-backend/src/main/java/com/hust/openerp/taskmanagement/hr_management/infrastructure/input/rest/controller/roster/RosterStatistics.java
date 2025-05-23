package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; // Import LocalDate
import java.util.List;
import java.util.UUID; // For createdShiftIds if it were here, but it's in RosterSolution

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RosterStatistics {
    // Add startDate and endDate for the roster period
    private LocalDate rosterStartDate;
    private LocalDate rosterEndDate;

    private long totalAssignedShifts;
    private double totalAssignedHours;
    private FairnessStats fairnessHours; // Renamed for clarity
    private FairnessCountStats fairnessSundayShifts;
    private FairnessCountStats fairnessNightShifts;
    private List<EmployeeStat> employeeStats;
    private List<String> detailedRosterLog;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FairnessStats { // For continuous values like hours
        private double minEmployeeValue;
        private double maxEmployeeValue;
        private double rangeValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FairnessCountStats { // For discrete counts like shifts
        private long minEmployeeCount;
        private long maxEmployeeCount;
        private long rangeCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeStat {
        private String staffCode; // Changed from employeeId to staffCode
        private String employeeName;
        private long totalShifts;
        private double totalHours;
        private int nightShifts;
        private int sundayShiftsWorked; // Changed from weekendDaysWorked to be specific
        private int saturdayShiftsWorked;
        private int maxConsecutiveWorkDays;
    }
}