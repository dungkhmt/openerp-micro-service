package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IShiftPort;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OrToolsSolverService {
    private final IShiftPort shiftPort;

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

    public List<ScheduledShift> solveRoster(RosterRequest request, List<StaffModel> employees, List<AbsenceModel> leaves, List<ShiftModel> existingShiftsData) {
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

        if (numEmployees == 0 || numShifts == 0 || numDays == 0) {
            System.out.println("Không có nhân viên, ca làm việc hoặc ngày để xếp lịch. Trả về lịch rỗng.");
            this.lastSolveFeasible = true; // Technically feasible as there's nothing to fail
            return new ArrayList<>();
        }

        Map<String, Integer> employeeIndexMap = IntStream.range(0, numEmployees)
            .boxed().collect(Collectors.toMap(i -> employees.get(i).getUserLoginId(), i -> i));

        BoolVar[][][] works = new BoolVar[numEmployees][numShifts][numDays];
        for (int e = 0; e < numEmployees; e++) {
            for (int s = 0; s < numShifts; s++) {
                for (int d = 0; d < numDays; d++) {
                    works[e][s][d] = (BoolVar) model.newBoolVar("works_e" + e + "_s" + s + "_d" + d);
                }
            }
        }

        // Helper variable: isWorkingOnDay[e][d] is true if employee e works any shift on day d.
        // Defined once here for use by multiple constraints.
        BoolVar[][] isWorkingOnDay = new BoolVar[numEmployees][numDays];
        for (int e = 0; e < numEmployees; e++) {
            for (int d = 0; d < numDays; d++) {
                isWorkingOnDay[e][d] = (BoolVar) model.newBoolVar("is_working_e" + e + "_d" + d);
                List<IntVar> shiftsThisDayList = new ArrayList<>();
                for (int s = 0; s < numShifts; s++) {
                    shiftsThisDayList.add(works[e][s][d]);
                }
                // isWorkingOnDay[e][d] = 1 <=> sum(shiftsThisDayList) >= 1
                // isWorkingOnDay[e][d] = 0 <=> sum(shiftsThisDayList) == 0
                model.addGreaterOrEqual(LinearExpr.sum(shiftsThisDayList.toArray(new IntVar[0])), 1)
                    .onlyEnforceIf(isWorkingOnDay[e][d]);
                model.addEquality(LinearExpr.sum(shiftsThisDayList.toArray(new IntVar[0])), 0)
                    .onlyEnforceIf(isWorkingOnDay[e][d].not());
            }
        }


        // --- APPLYING CONSTRAINTS ---

        // 1. Min/Max employees per shift (Standard constraint, always applied based on ShiftDefinition)
        for (int d = 0; d < numDays; d++) {
            for (int s = 0; s < numShifts; s++) {
                ShiftDefinition currentShiftDef = shiftDefs.get(s);
                List<IntVar> assignedToShiftOnDay = new ArrayList<>();
                for (int e = 0; e < numEmployees; e++) {
                    assignedToShiftOnDay.add(works[e][s][d]);
                }
                model.addGreaterOrEqual(LinearExpr.sum(assignedToShiftOnDay.toArray(new IntVar[0])), currentShiftDef.getMinEmployees());
                if (currentShiftDef.getMaxEmployees() != null && currentShiftDef.getMaxEmployees() > 0) {
                    model.addLessOrEqual(LinearExpr.sum(assignedToShiftOnDay.toArray(new IntVar[0])), currentShiftDef.getMaxEmployees());
                }
            }
        }

        // 2. MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE
        if (hardConstraints.containsKey("MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE")) {
            Object param = hardConstraints.get("MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE");
            if (param instanceof Map) {
                Object countObj = ((Map<?, ?>) param).get("count");
                if (countObj instanceof Number) {
                    int maxShifts = ((Number) countObj).intValue();
                    if (maxShifts > 0) {
                        for (int e = 0; e < numEmployees; e++) {
                            for (int d = 0; d < numDays; d++) {
                                List<IntVar> shiftsForEmployeeOnDay = new ArrayList<>();
                                for (int s = 0; s < numShifts; s++) {
                                    shiftsForEmployeeOnDay.add(works[e][s][d]);
                                }
                                model.addLessOrEqual(LinearExpr.sum(shiftsForEmployeeOnDay.toArray(new IntVar[0])), maxShifts);
                            }
                        }
                    }
                }
            }
        }

        // 3. ENSURE_EMPLOYEE_APPROVED_LEAVE
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
                                    model.addEquality(works[empIdx][s][dayIdx], 0);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing leave date for ENSURE_EMPLOYEE_APPROVED_LEAVE: " + leave.getDate() + " for user " + leave.getUserId() + ". Error: " + e.getMessage());
                    }
                }
            }
        }

        // 4. NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT
        if (Boolean.TRUE.equals(hardConstraints.get("NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT"))) {
            for (int e = 0; e < numEmployees; e++) {
                for (int d = 0; d < numDays - 1; d++) { // Iterate up to the second to last day
                    for (int sNight = 0; sNight < numShifts; sNight++) {
                        if (shiftDefs.get(sNight).isNightShift()) {
                            List<IntVar> allShiftsNextDay = new ArrayList<>();
                            for (int sNext = 0; sNext < numShifts; sNext++) {
                                allShiftsNextDay.add(works[e][sNext][d + 1]);
                            }
                            model.addEquality(LinearExpr.sum(allShiftsNextDay.toArray(new IntVar[0])), 0)
                                .onlyEnforceIf(works[e][sNight][d]);
                        }
                    }
                }
            }
        }

        // 5. MAX_CONSECUTIVE_WORK_DAYS
        if (hardConstraints.containsKey("MAX_CONSECUTIVE_WORK_DAYS")) {
            Object param = hardConstraints.get("MAX_CONSECUTIVE_WORK_DAYS");
            if (param instanceof Map) {
                Object daysObj = ((Map<?, ?>) param).get("days");
                if (daysObj instanceof Number) {
                    int maxConsecutive = ((Number) daysObj).intValue();
                    if (maxConsecutive > 0 && numDays > maxConsecutive) {
                        for (int e = 0; e < numEmployees; e++) {
                            for (int d = 0; d <= numDays - (maxConsecutive + 1); d++) {
                                List<IntVar> window = new ArrayList<>();
                                for (int i = 0; i < maxConsecutive + 1; i++) {
                                    window.add(isWorkingOnDay[e][d + i]); // Use the pre-defined isWorkingOnDay
                                }
                                model.addLessOrEqual(LinearExpr.sum(window.toArray(new IntVar[0])), maxConsecutive);
                            }
                        }
                    }
                }
            }
        }

        // 6. AVOID_OVERLAPPING_EXISTING_SHIFTS
        if (Boolean.TRUE.equals(hardConstraints.get("AVOID_OVERLAPPING_EXISTING_SHIFTS"))) {
            List<String> employeeUserLoginIds = employees.stream().map(StaffModel::getUserLoginId).collect(Collectors.toList());

            if (existingShiftsData != null && !existingShiftsData.isEmpty()) {
                System.out.println("INFO: Applying constraint: AVOID_OVERLAPPING_EXISTING_SHIFTS with " + existingShiftsData.size() + " existing shifts.");
                Map<String, Map<LocalDate, List<LocalTime[]>>> employeeDailyIntervals = new HashMap<>();
                for (ShiftModel es : existingShiftsData) {
                    String userId = es.getUserId();
                    LocalDate esDate = es.getDate(); // ShiftModel now uses getDate()
                    LocalTime esStartTime = es.getStartTime(); // ShiftModel now has LocalTime
                    LocalTime esEndTime = es.getEndTime();   // ShiftModel now has LocalTime

                    if (userId == null || esDate == null || esStartTime == null || esEndTime == null) {
                        System.err.println("Skipping existing shift due to null fields for user " + userId);
                        continue;
                    }

                    employeeDailyIntervals.computeIfAbsent(userId, k -> new HashMap<>());
                    if (esEndTime.isAfter(esStartTime) || esEndTime.equals(esStartTime)) {
                        employeeDailyIntervals.get(userId).computeIfAbsent(esDate, k -> new ArrayList<>()).add(new LocalTime[]{esStartTime, esEndTime});
                    } else { // Crosses midnight
                        employeeDailyIntervals.get(userId).computeIfAbsent(esDate, k -> new ArrayList<>()).add(new LocalTime[]{esStartTime, LocalTime.MAX});
                        employeeDailyIntervals.get(userId).computeIfAbsent(esDate.plusDays(1), k -> new ArrayList<>()).add(new LocalTime[]{LocalTime.MIN, esEndTime});
                    }
                }

                for (int e = 0; e < numEmployees; e++) {
                    StaffModel currentEmployee = employees.get(e);
                    Map<LocalDate, List<LocalTime[]>> existingIntervalsForEmp = employeeDailyIntervals.get(currentEmployee.getUserLoginId());
                    if (existingIntervalsForEmp == null || existingIntervalsForEmp.isEmpty()) continue;

                    for (int d = 0; d < numDays; d++) {
                        LocalDate currentDate = dateRange.get(d);
                        List<LocalTime[]> existingIntervalsOnCurrentDay = existingIntervalsForEmp.get(currentDate);

                        for (int s = 0; s < numShifts; s++) {
                            ShiftDefinition newShiftDef = shiftDefs.get(s);
                            LocalTime newShiftStartTime, newShiftEndTime;
                            try {
                                newShiftStartTime = convertStringToLocalTime(newShiftDef.getStartTime());
                                newShiftEndTime = convertStringToLocalTime(newShiftDef.getEndTime());
                                if (newShiftStartTime == null || newShiftEndTime == null) throw new DateTimeParseException("Parsed time is null", "", 0);
                            } catch (DateTimeParseException ex) {
                                System.err.println("Skipping overlap check for new shift " + newShiftDef.getName() + " due to parse error: " + ex.getMessage());
                                continue;
                            }

                            // Check part on current day
                            if (existingIntervalsOnCurrentDay != null) {
                                LocalTime effectiveNewEnd = (newShiftEndTime.isAfter(newShiftStartTime) || newShiftEndTime.equals(newShiftStartTime)) ? newShiftEndTime : LocalTime.MAX;
                                for (LocalTime[] existingInt : existingIntervalsOnCurrentDay) {
                                    if (newShiftStartTime.isBefore(existingInt[1]) && effectiveNewEnd.isAfter(existingInt[0])) {
                                        model.addEquality(works[e][s][d], 0);
                                        // System.out.printf("OVERLAP (current_day): Emp %s, Day %s, New Shift %s vs Existing. works=0.%n", currentEmployee.getUserLoginId(), currentDate, newShiftDef.getName());
                                        break;
                                    }
                                }
                            }

                            // Check part on next day if new shift crosses midnight
                            if (newShiftEndTime.isBefore(newShiftStartTime)) {
                                LocalDate nextDayDate = currentDate.plusDays(1);
                                int nextDayIdx = (int) ChronoUnit.DAYS.between(startDate, nextDayDate);
                                if (nextDayIdx >= 0 && nextDayIdx < numDays) { // Ensure next day is in planning period
                                    List<LocalTime[]> existingIntervalsOnNextDay = existingIntervalsForEmp.get(nextDayDate);
                                    if (existingIntervalsOnNextDay != null) {
                                        LocalTime part2Start = LocalTime.MIN;
                                        LocalTime part2End = newShiftEndTime;
                                        for (LocalTime[] existingIntNext : existingIntervalsOnNextDay) {
                                            if (part2Start.isBefore(existingIntNext[1]) && part2End.isAfter(existingIntNext[0])) {
                                                model.addEquality(works[e][s][d], 0);
                                                // System.out.printf("OVERLAP (next_day_part): Emp %s, Day %s, New Shift %s vs Existing on %s. works=0.%n", currentEmployee.getUserLoginId(), currentDate, newShiftDef.getName(), nextDayDate);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("INFO: AVOID_OVERLAPPING_EXISTING_SHIFTS is active, but no existing shifts data found/provided to check against.");
            }
        }


        // 7. MIN_REST_BETWEEN_SHIFTS_HOURS
        if (hardConstraints.containsKey("MIN_REST_BETWEEN_SHIFTS_HOURS")) {
            Object param = hardConstraints.get("MIN_REST_BETWEEN_SHIFTS_HOURS");
            if (param instanceof Map) {
                Object hoursObj = ((Map<?, ?>) param).get("hours");
                if (hoursObj instanceof Number) {
                    long minRestHours = ((Number) hoursObj).longValue();
                    if (minRestHours > 0) {
                        for (int e = 0; e < numEmployees; e++) {
                            for (int d = 0; d < numDays; d++) {
                                for (int s1 = 0; s1 < numShifts; s1++) {
                                    ShiftDefinition shift1Def = shiftDefs.get(s1);
                                    LocalTime shift1End = convertStringToLocalTime(shift1Def.getEndTime());
                                    if (shift1End == null) continue;

                                    // Check with subsequent shifts on the same day (if MAX_SHIFTS_PER_DAY > 1)
                                    // Note: This assumes MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE handles if multiple shifts can be assigned.
                                    // If only 1 shift per day, this inner s2 loop is not strictly needed for same day.
                                    for (int s2 = 0; s2 < numShifts; s2++) {
                                        if (s1 == s2) continue;
                                        ShiftDefinition shift2Def = shiftDefs.get(s2);
                                        LocalTime shift2Start = convertStringToLocalTime(shift2Def.getStartTime());
                                        if (shift2Start == null) continue;

                                        if (shift2Start.isAfter(shift1End)) { // s2 is after s1 on the same day
                                            if (Duration.between(shift1End, shift2Start).toHours() < minRestHours) {
                                                // If e works s1, then e cannot work s2
                                                model.addImplication(works[e][s1][d], works[e][s2][d].not());
                                            }
                                        }
                                    }

                                    // Check with shifts on the next day (d+1)
                                    if (d < numDays - 1) {
                                        for (int sNext = 0; sNext < numShifts; sNext++) {
                                            ShiftDefinition shiftNextDef = shiftDefs.get(sNext);
                                            LocalTime shiftNextStart = convertStringToLocalTime(shiftNextDef.getStartTime());
                                            if (shiftNextStart == null) continue;

                                            // Calculate duration from shift1End (day d) to shiftNextStart (day d+1)
                                            long restDurationMillis = Duration.between(shift1End, LocalTime.MAX).toMillis() + 1; // Time to end of day d
                                            restDurationMillis += Duration.between(LocalTime.MIN, shiftNextStart).toMillis(); // Time from start of day d+1

                                            if ((restDurationMillis / (1000.0 * 60 * 60)) < minRestHours) {
                                                model.addImplication(works[e][s1][d], works[e][sNext][d+1].not());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        // 8. MAX_WEEKLY_WORK_HOURS
        if (hardConstraints.containsKey("MAX_WEEKLY_WORK_HOURS")) {
            Object param = hardConstraints.get("MAX_WEEKLY_WORK_HOURS");
            if (param instanceof Map) {
                Object hoursObj = ((Map<?, ?>) param).get("hours");
                if (hoursObj instanceof Number) {
                    long maxWeeklyWorkMillis = ((Number) hoursObj).longValue() * 60 * 60 * 1000; // Convert hours to milliseconds
                    if (maxWeeklyWorkMillis > 0) {
                        for (int e = 0; e < numEmployees; e++) {
                            for (int d = 0; d <= numDays - 7; d++) { // Sliding window of 7 days
                                List<LinearExpr> weeklyWorkExpressions = new ArrayList<>();
                                List<BoolVar> varsInSum = new ArrayList<>(); // for weighted sum

                                for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
                                    int currentDayIdx = d + dayOffset;
                                    for (int s = 0; s < numShifts; s++) {
                                        ShiftDefinition shiftDef = shiftDefs.get(s);
                                        LocalTime st = convertStringToLocalTime(shiftDef.getStartTime());
                                        LocalTime et = convertStringToLocalTime(shiftDef.getEndTime());
                                        if (st == null || et == null) continue;

                                        long durationMillis;
                                        if (et.isAfter(st) || et.equals(st)) {
                                            durationMillis = Duration.between(st, et).toMillis();
                                        } else { // Crosses midnight
                                            durationMillis = Duration.between(st, LocalTime.MAX).toMillis() + 1 + Duration.between(LocalTime.MIN, et).toMillis();
                                        }
                                        varsInSum.add(works[e][s][currentDayIdx]);
                                        weeklyWorkExpressions.add(LinearExpr.term(works[e][s][currentDayIdx], durationMillis));
                                    }
                                }
                                if (!weeklyWorkExpressions.isEmpty()) {
                                    model.addLessOrEqual(LinearExpr.sum(weeklyWorkExpressions.toArray(new LinearExpr[0])), maxWeeklyWorkMillis);
                                }
                            }
                        }
                    }
                }
            }
        }

        // 9. NO_CLASHING_SHIFTS_FOR_EMPLOYEE (Boolean toggle)
        // This constraint is typically ensured if MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE is set to 1.
        // If MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE can be > 1 and this is true,
        // it would require explicit pairwise time overlap check for co-assigned shifts.
        // For now, assuming its intent is covered by MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE = 1.
        // If this needs more complex logic (e.g. employee can work two shifts if their times don't overlap):
        if (Boolean.TRUE.equals(hardConstraints.get("NO_CLASHING_SHIFTS_FOR_EMPLOYEE"))) {
            // This is a complex constraint if an employee can have multiple shifts per day.
            // It means that if works[e][s1][d] and works[e][s2][d] are both true,
            // then shift s1 and shift s2 must not overlap in time.
            // Current model with works[e][s][d] and MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE=1 usually handles this.
            // If MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE > 1, this needs explicit pairwise conflict:
            // For e, d, s1, s2 (s1 != s2):
            //   If shiftDef[s1] time overlaps shiftDef[s2] time:
            //     model.addLessOrEqual(LinearExpr.sum(new IntVar[]{works[e][s1][d], works[e][s2][d]}), 1);
            // This is automatically handled if MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE is 1.
            // If MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE is > 1, and this flag is true, the above pairwise would be needed.
            // For now, we print a warning if MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE is not 1 as it might not behave as expected.
            Object maxShiftsParam = hardConstraints.get("MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE");
            int maxShifts = 1; // Default if not specified for this check
            if (maxShiftsParam instanceof Map) {
                Object countObj = ((Map<?, ?>) maxShiftsParam).get("count");
                if (countObj instanceof Number) maxShifts = ((Number) countObj).intValue();
            }
            if (maxShifts > 1) {
                System.out.println("WARNING: NO_CLASHING_SHIFTS_FOR_EMPLOYEE is active, and MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE > 1. " +
                    "The current model does not explicitly prevent time-based clashes for multiple assigned shifts on the same day. " +
                    "It only ensures the *count* of shifts is met. True non-clashing would require pairwise shift definition comparison.");
                // For a full implementation with MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE > 1:
                for (int e = 0; e < numEmployees; e++) {
                    for (int d = 0; d < numDays; d++) {
                        for (int s1_idx = 0; s1_idx < numShifts; s1_idx++) {
                            for (int s2_idx = s1_idx + 1; s2_idx < numShifts; s2_idx++) {
                                ShiftDefinition def1 = shiftDefs.get(s1_idx);
                                ShiftDefinition def2 = shiftDefs.get(s2_idx);
                                LocalTime start1 = convertStringToLocalTime(def1.getStartTime());
                                LocalTime end1 = convertStringToLocalTime(def1.getEndTime());
                                LocalTime start2 = convertStringToLocalTime(def2.getStartTime());
                                LocalTime end2 = convertStringToLocalTime(def2.getEndTime());

                                if (start1 == null || end1 == null || start2 == null || end2 == null) continue;

                                // Normalize for midnight crossing for comparison on a single 24h cycle
                                LocalTime effEnd1 = end1.isBefore(start1) ? LocalTime.MAX : end1;
                                LocalTime effEnd2 = end2.isBefore(start2) ? LocalTime.MAX : end2;
                                // This simple check here is for shifts on the same day cycle.
                                // Cross-midnight shifts would make this more complex.
                                boolean timeOverlap = start1.isBefore(effEnd2) && effEnd1.isAfter(start2);

                                if (timeOverlap) {
                                    // If shift s1 and s2 overlap in time, employee e cannot work both.
                                    model.addLessOrEqual(LinearExpr.sum(new IntVar[]{works[e][s1_idx][d], works[e][s2_idx][d]}), 1);
                                }
                            }
                        }
                    }
                }

            }
        }


        // 10. MIN_WEEKEND_DAYS_OFF_PER_PERIOD
        if (hardConstraints.containsKey("MIN_WEEKEND_DAYS_OFF_PER_PERIOD")) {
            Object param = hardConstraints.get("MIN_WEEKEND_DAYS_OFF_PER_PERIOD");
            if (param instanceof Map) {
                Object countObj = ((Map<?, ?>) param).get("count");
                Object periodWeeksObj = ((Map<?, ?>) param).get("periodWeeks");

                if (countObj instanceof Number && periodWeeksObj instanceof Number) {
                    int minWeekendDaysOff = ((Number) countObj).intValue();
                    int periodWeeks = ((Number) periodWeeksObj).intValue();

                    if (minWeekendDaysOff >= 0 && periodWeeks > 0) {
                        int windowDays = periodWeeks * 7;
                        for (int e = 0; e < numEmployees; e++) {
                            for (int dStartWindow = 0; dStartWindow <= numDays - windowDays; dStartWindow++) {
                                List<Literal> weekendOffLiteralsInWindow = new ArrayList<>();
                                for (int dayOffset = 0; dayOffset < windowDays; dayOffset++) {
                                    int currentDayIdx = dStartWindow + dayOffset;
                                    LocalDate currentDate = dateRange.get(currentDayIdx);
                                    DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                                    if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                                        // isWorkingOnDay[e][currentDayIdx].not() means employee is OFF on this weekend day
                                        weekendOffLiteralsInWindow.add(isWorkingOnDay[e][currentDayIdx].not());
                                    }
                                }
                                if (!weekendOffLiteralsInWindow.isEmpty()) {
                                    model.addGreaterOrEqual(LinearExpr.sum(weekendOffLiteralsInWindow.toArray(new Literal[0])), minWeekendDaysOff);
                                }
                            }
                        }
                    }
                }
            }
        }


        // --- SOLVER ---
        CpSolver solver = new CpSolver();
        solver.getParameters().setLogSearchProgress(true); // Useful for debugging performance
        solver.getParameters().setMaxTimeInSeconds(30.0); // Adjust as needed

        System.out.println("Starting solver...");
        CpSolverStatus status = solver.solve(model);
        System.out.println("Solver finished with status: " + status);

        List<ScheduledShift> generatedSchedule = new ArrayList<>();
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            this.lastSolveFeasible = true;
            System.out.println("\n🎉 SOLUTION FOUND! DETAILED ROSTER:");
            System.out.println("=========================================================");
            // (Detailed roster printing logic - keep as is from previous version)
            for (int d_print = 0; d_print < numDays; d_print++) {
                LocalDate currentDate_print = dateRange.get(d_print);
                System.out.println("\n📅 DATE: " + currentDate_print.format(DateTimeFormatter.ISO_LOCAL_DATE) + " (" + currentDate_print.getDayOfWeek() + ")");
                boolean dayHasAssignments = false;

                for (int s_print = 0; s_print < numShifts; s_print++) {
                    ShiftDefinition shiftDef_print = shiftDefs.get(s_print);
                    List<String> employeesOnThisShift = new ArrayList<>();
                    for (int e_print = 0; e_print < numEmployees; e_print++) {
                        if (solver.booleanValue(works[e_print][s_print][d_print])) {
                            employeesOnThisShift.add(employees.get(e_print).getFullname() + " (" + employees.get(e_print).getUserLoginId() + ")");
                        }
                    }
                    if (!employeesOnThisShift.isEmpty()) {
                        System.out.println("  🕒 SHIFT: " + shiftDef_print.getName() + " (" + shiftDef_print.getStartTime() + " - " + shiftDef_print.getEndTime() + ")");
                        for (String empName : employeesOnThisShift) {
                            System.out.println("    👤 " + empName);
                        }
                        dayHasAssignments = true;
                    }
                }
                if (!dayHasAssignments) {
                    System.out.println("  (No assignments for this day)");
                }
            }
            System.out.println("=========================================================");


            // Populate and save shifts
            for (int d = 0; d < numDays; d++) {
                LocalDate currentDate = dateRange.get(d);
                for (int e = 0; e < numEmployees; e++) {
                    for (int s = 0; s < numShifts; s++) {
                        if (solver.booleanValue(works[e][s][d])) {
                            StaffModel emp = employees.get(e);
                            ShiftDefinition shiftDef = shiftDefs.get(s);

                            LocalTime localStartTime = convertStringToLocalTime(shiftDef.getStartTime());
                            LocalTime localEndTime = convertStringToLocalTime(shiftDef.getEndTime());

                            if (localStartTime != null && localEndTime != null) {
                                ShiftModel shiftToSave = ShiftModel.builder()
                                    .userId(emp.getUserLoginId())
                                    .note(shiftDef.getName())
                                    .date(currentDate)
                                    .startTime(localStartTime)
                                    .endTime(localEndTime)
                                    .build();
                                shiftPort.createShift(shiftToSave);
                            } else {
                                System.err.println("Could not save shift due to time parsing error for " + shiftDef.getName());
                            }

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
            System.out.println("\n❌ NO SOLUTION FOUND or an error occurred. Status: " + status);
            if (status == CpSolverStatus.INFEASIBLE) {
                System.out.println("The problem is INFEASIBLE. Consider reviewing constraints, input data (employee availability vs shift demands), or leave requests.");
            } else if (status == CpSolverStatus.MODEL_INVALID) {
                System.out.println("The model is INVALID. There might be an issue with how constraints are defined or with inconsistent data.");
            }
        }
        return generatedSchedule;
    }

    public static LocalTime convertStringToLocalTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            // System.err.println("Time string is null or empty."); // Reduced verbosity
            return null;
        }
        try {
            return LocalTime.parse(timeString);
        } catch (DateTimeParseException e) {
            System.err.println("Error converting string '" + timeString + "' to LocalTime. Expected format HH:mm. Error: " + e.getMessage());
            return null;
        }
    }
}
