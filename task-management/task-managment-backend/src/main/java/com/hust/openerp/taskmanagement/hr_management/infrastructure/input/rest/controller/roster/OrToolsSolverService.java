package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import com.google.ortools.Loader;
import com.google.ortools.sat.*; // Keep this for CpModel, CpSolver, etc.
import com.google.ortools.sat.BoolVar; // Explicit import for clarity, though sat.* covers it
import com.google.ortools.sat.IntVar; // Explicit import for clarity
import com.google.ortools.sat.Literal; // Explicit import for clarity
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OrToolsSolverService {
    static {
        try {
            Loader.loadNativeLibraries();
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load. Check OR-Tools setup.\n" + e);
            System.exit(1);
        }
    }
    private boolean lastSolveFeasible = false;

    public boolean wasLastSolveFeasible() {
        return lastSolveFeasible;
    }


    public List<ScheduledShift> solveRoster(RosterRequest request, List<StaffModel> employees, List<AbsenceModel> leaves) {
        this.lastSolveFeasible = false;
        CpModel model = new CpModel();

        List<ShiftDefinition> shiftDefs = request.getDefinedShifts();
        Map<String, Object> hardConstraints = request.getActiveHardConstraints();

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        List<LocalDate> dateRange = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
        int numDays = dateRange.size();
        int numEmployees = employees.size();
        int numShifts = shiftDefs.size();

        Map<String, Integer> employeeIndexMap = IntStream.range(0, numEmployees)
            .boxed().collect(Collectors.toMap(i -> employees.get(i).getUserLoginId(), i -> i));

        // works[e][s][d] is 1 if employee e works shift s on day d, 0 otherwise.
        // Declare as BoolVar and cast from model.newBoolVar()
        BoolVar[][][] works = new BoolVar[numEmployees][numShifts][numDays];
        for (int e = 0; e < numEmployees; e++) {
            for (int s = 0; s < numShifts; s++) {
                for (int d = 0; d < numDays; d++) {
                    // Cast the result of newBoolVar to BoolVar
                    works[e][s][d] = (BoolVar) model.newBoolVar("works_e" + e + "_s" + s + "_d" + d);
                }
            }
        }

        // 1. Min/Max employees per shift
        for (int d = 0; d < numDays; d++) {
            for (int s = 0; s < numShifts; s++) {
                ShiftDefinition currentShiftDef = shiftDefs.get(s);
                List<IntVar> assignedToShiftOnDay = new ArrayList<>(); // List<IntVar> is fine as BoolVar is an IntVar
                for (int e = 0; e < numEmployees; e++) {
                    assignedToShiftOnDay.add(works[e][s][d]);
                }
                model.addGreaterOrEqual(LinearExpr.sum(assignedToShiftOnDay.toArray(new IntVar[0])), currentShiftDef.getMinEmployees());
                if (currentShiftDef.getMaxEmployees() != null && currentShiftDef.getMaxEmployees() > 0) {
                    model.addLessOrEqual(LinearExpr.sum(assignedToShiftOnDay.toArray(new IntVar[0])), currentShiftDef.getMaxEmployees());
                }
            }
        }

        // 2. Max shifts per day per employee
        Object maxShiftsPerDayParam = hardConstraints.get("MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE");
        int maxShiftsPerDay = 1;
        if (maxShiftsPerDayParam instanceof Map) {
            Object countObj = ((Map<?, ?>) maxShiftsPerDayParam).get("count");
            if (countObj instanceof Number) {
                maxShiftsPerDay = ((Number) countObj).intValue();
            }
        }
        for (int e = 0; e < numEmployees; e++) {
            for (int d = 0; d < numDays; d++) {
                List<IntVar> shiftsForEmployeeOnDay = new ArrayList<>();
                for (int s = 0; s < numShifts; s++) {
                    shiftsForEmployeeOnDay.add(works[e][s][d]);
                }
                model.addLessOrEqual(LinearExpr.sum(shiftsForEmployeeOnDay.toArray(new IntVar[0])), maxShiftsPerDay);
            }
        }

        // 3. Ensure employee approved leave
        if (Boolean.TRUE.equals(hardConstraints.get("ENSURE_EMPLOYEE_APPROVED_LEAVE"))) {
            for (AbsenceModel leave : leaves) {
                Integer empIdx = employeeIndexMap.get(leave.getUserId());
                if (empIdx != null) {
                    try {
                        LocalDate leaveDate = leave.getDate();
                        if (!leaveDate.isBefore(startDate) && !leaveDate.isAfter(endDate)) {
                            int dayIdx = (int) ChronoUnit.DAYS.between(startDate, leaveDate);
                            if (dayIdx >= 0 && dayIdx < numDays) {
                                for (int s = 0; s < numShifts; s++) {
                                    model.addEquality(works[empIdx][s][dayIdx], 0); // works is BoolVar, equality with 0 is fine
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing leave date: " + leave.getDate() + " for user " + leave.getUserId() + ". Error: " + e.getMessage());
                    }
                }
            }
        }

        // 4. No work next day after night shift
        if (Boolean.TRUE.equals(hardConstraints.get("NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT"))) {
            for (int e = 0; e < numEmployees; e++) {
                for (int d = 0; d < numDays - 1; d++) {
                    for (int sNight = 0; sNight < numShifts; sNight++) {
                        if (shiftDefs.get(sNight).isNightShift()) {
                            List<IntVar> allShiftsNextDay = new ArrayList<>();
                            for (int sNext = 0; sNext < numShifts; sNext++) {
                                allShiftsNextDay.add(works[e][sNext][d + 1]);
                            }
                            // works[e][sNight][d] is now BoolVar, which is a Literal
                            model.addEquality(LinearExpr.sum(allShiftsNextDay.toArray(new IntVar[0])), 0)
                                .onlyEnforceIf(works[e][sNight][d]); // This should now work
                        }
                    }
                }
            }
        }

        // 5. Max consecutive work days
        Object maxConsecutiveParam = hardConstraints.get("MAX_CONSECUTIVE_WORK_DAYS");
        if (maxConsecutiveParam instanceof Map) {
            Object daysObj = ((Map<?, ?>) maxConsecutiveParam).get("days");
            if (daysObj instanceof Number) {
                int maxConsecutiveDays = ((Number) daysObj).intValue();
                if (maxConsecutiveDays > 0 && numDays > maxConsecutiveDays) {
                    // Declare as BoolVar and cast
                    BoolVar[][] isWorkingOnDay = new BoolVar[numEmployees][numDays];
                    for(int e=0; e < numEmployees; e++) {
                        for(int d=0; d < numDays; d++) {
                            // Cast the result of newBoolVar to BoolVar
                            isWorkingOnDay[e][d] = (BoolVar) model.newBoolVar("is_working_e"+e+"_d"+d);
                            List<IntVar> shiftsThisDayList = new ArrayList<>();
                            for(int s=0; s<numShifts; s++) {
                                shiftsThisDayList.add(works[e][s][d]);
                            }

                            // isWorkingOnDay[e][d] is BoolVar (Literal)
                            model.addGreaterOrEqual(LinearExpr.sum(shiftsThisDayList.toArray(new IntVar[0])), 1)
                                .onlyEnforceIf(isWorkingOnDay[e][d]);
                            // isWorkingOnDay[e][d].not() should now work
                            model.addEquality(LinearExpr.sum(shiftsThisDayList.toArray(new IntVar[0])), 0)
                                .onlyEnforceIf(isWorkingOnDay[e][d].not());
                        }
                    }

                    for (int e = 0; e < numEmployees; e++) {
                        for (int d = 0; d <= numDays - (maxConsecutiveDays + 1); d++) {
                            List<IntVar> window = new ArrayList<>(); // List of BoolVar, still compatible with List<IntVar>
                            for (int i = 0; i < maxConsecutiveDays + 1; i++) {
                                window.add(isWorkingOnDay[e][d + i]);
                            }
                            model.addLessOrEqual(LinearExpr.sum(window.toArray(new IntVar[0])), maxConsecutiveDays);
                        }
                    }
                }
            }
        }

        CpSolver solver = new CpSolver();
        solver.getParameters().setLogSearchProgress(true);
        solver.getParameters().setMaxTimeInSeconds(30.0);

        CpSolverStatus status = solver.solve(model);
        System.out.println("Solver status: " + status);

        List<ScheduledShift> generatedSchedule = new ArrayList<>();
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            this.lastSolveFeasible = true;
            System.out.println("Solution found!");
            for (int d = 0; d < numDays; d++) {
                LocalDate currentDate = dateRange.get(d);
                for (int e = 0; e < numEmployees; e++) {
                    for (int s = 0; s < numShifts; s++) {
                        // works[e][s][d] is BoolVar (Literal), solver.booleanValue() should work
                        if (solver.booleanValue(works[e][s][d])) {
                            StaffModel emp = employees.get(e);
                            ShiftDefinition shiftDef = shiftDefs.get(s);
                            generatedSchedule.add(new ScheduledShift(
                                emp.getUserLoginId(),
                                emp.getFullname(),
                                shiftDef.getName(),
                                currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                                shiftDef.getStartTime(),
                                shiftDef.getEndTime()
                            ));
                        }
                    }
                }
            }
        } else {
            this.lastSolveFeasible = false;
            System.out.println("No solution found or an error occurred. Status: " + status);
        }
        return generatedSchedule;
    }
}
