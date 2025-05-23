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

    // Update return type
    public RosterSolution solveRoster(RosterRequest request, List<StaffModel> employees, List<AbsenceModel> leaves, List<ShiftModel> existingShiftsDataParam) {
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

        RosterStatistics.RosterStatisticsBuilder statsBuilder = RosterStatistics.builder();
        List<String> detailedRosterLogForStats = new ArrayList<>();


        if (numEmployees == 0 || numShifts == 0 || numDays == 0) {
            System.out.println("Không có nhân viên, ca làm việc hoặc ngày để xếp lịch. Trả về lịch rỗng.");
            this.lastSolveFeasible = true;
            return RosterSolution.builder()
                .scheduledShifts(new ArrayList<>())
                .statistics(statsBuilder.employeeStats(new ArrayList<>()).detailedRosterLog(detailedRosterLogForStats).build()) // Return empty stats
                .build();
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

        BoolVar[][] isWorkingOnDay = new BoolVar[numEmployees][numDays];
        for (int e = 0; e < numEmployees; e++) {
            for (int d = 0; d < numDays; d++) {
                isWorkingOnDay[e][d] = (BoolVar) model.newBoolVar("is_working_e" + e + "_d" + d);
                List<IntVar> shiftsThisDayList = new ArrayList<>();
                for (int s = 0; s < numShifts; s++) {
                    shiftsThisDayList.add(works[e][s][d]);
                }
                model.addGreaterOrEqual(LinearExpr.sum(shiftsThisDayList.toArray(new IntVar[0])), 1)
                    .onlyEnforceIf(isWorkingOnDay[e][d]);
                model.addEquality(LinearExpr.sum(shiftsThisDayList.toArray(new IntVar[0])), 0)
                    .onlyEnforceIf(isWorkingOnDay[e][d].not());
            }
        }

        // --- APPLYING CONSTRAINTS ---
        // (Constraints 1-5, 7-10 remain the same as your provided code)
        // Constraint 1: Min/Max employees per shift
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

        // Constraint 2: MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE
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
        // Constraint 3: ENSURE_EMPLOYEE_APPROVED_LEAVE (Keep as is)
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

        // Constraint 4: NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT (Keep as is)
        if (Boolean.TRUE.equals(hardConstraints.get("NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT"))) {
            for (int e = 0; e < numEmployees; e++) {
                for (int d = 0; d < numDays - 1; d++) { // Iterate up to the second to last day
                    for (int sNight = 0; sNight < numShifts; sNight++) {
                        if (shiftDefs.get(sNight).getIsNightShift()) { // Use getter
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
        // Constraint 5: MAX_CONSECUTIVE_WORK_DAYS (Keep as is)
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

        // Constraint 6: AVOID_OVERLAPPING_EXISTING_SHIFTS (Keep as is, using existingShiftsDataParam)
        if (Boolean.TRUE.equals(hardConstraints.get("AVOID_OVERLAPPING_EXISTING_SHIFTS"))) {
            if (existingShiftsDataParam != null && !existingShiftsDataParam.isEmpty()) {
                System.out.println("INFO: Applying constraint: AVOID_OVERLAPPING_EXISTING_SHIFTS with " + existingShiftsDataParam.size() + " existing shifts.");
                Map<String, Map<LocalDate, List<LocalTime[]>>> employeeDailyIntervals = new HashMap<>();
                for (ShiftModel es : existingShiftsDataParam) {
                    String userId = es.getUserId();
                    LocalDate esDate = es.getDate();
                    LocalTime esStartTime = es.getStartTime();
                    LocalTime esEndTime = es.getEndTime();

                    if (userId == null || esDate == null || esStartTime == null || esEndTime == null) {
                        System.err.println("Skipping existing shift due to null fields for user " + userId);
                        continue;
                    }

                    employeeDailyIntervals.computeIfAbsent(userId, k -> new HashMap<>());
                    if (esEndTime.isAfter(esStartTime) || esEndTime.equals(esStartTime)) {
                        employeeDailyIntervals.get(userId).computeIfAbsent(esDate, k -> new ArrayList<>()).add(new LocalTime[]{esStartTime, esEndTime});
                    } else {
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
                            boolean overlapDetectedThisIteration = false;
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

                            if (existingIntervalsOnCurrentDay != null) {
                                LocalTime effectiveNewEnd = (newShiftEndTime.isAfter(newShiftStartTime) || newShiftEndTime.equals(newShiftStartTime)) ? newShiftEndTime : LocalTime.MAX;
                                for (LocalTime[] existingInt : existingIntervalsOnCurrentDay) {
                                    if (newShiftStartTime.isBefore(existingInt[1]) && effectiveNewEnd.isAfter(existingInt[0])) {
                                        model.addEquality(works[e][s][d], 0);
                                        overlapDetectedThisIteration = true;
                                        break;
                                    }
                                }
                            }
                            if (overlapDetectedThisIteration) continue;

                            if (newShiftEndTime.isBefore(newShiftStartTime)) {
                                LocalDate nextDayDate = currentDate.plusDays(1);
                                int nextDayIdx = (int) ChronoUnit.DAYS.between(startDate, nextDayDate);
                                if (nextDayIdx >= 0 && nextDayIdx < numDays) {
                                    List<LocalTime[]> existingIntervalsOnNextDay = existingIntervalsForEmp.get(nextDayDate);
                                    if (existingIntervalsOnNextDay != null) {
                                        LocalTime part2Start = LocalTime.MIN;
                                        LocalTime part2End = newShiftEndTime;
                                        for (LocalTime[] existingIntNext : existingIntervalsOnNextDay) {
                                            if (part2Start.isBefore(existingIntNext[1]) && part2End.isAfter(existingIntNext[0])) {
                                                model.addEquality(works[e][s][d], 0);
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
                System.out.println("INFO: AVOID_OVERLAPPING_EXISTING_SHIFTS is active, but no existing shifts data passed to check against.");
            }
        }

        // Constraint 7: MIN_REST_BETWEEN_SHIFTS_HOURS (Keep as is)
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

                                    Object maxShiftsParamForRestCheck = hardConstraints.get("MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE");
                                    int maxShiftsPerDayForRest = 1;
                                    if (maxShiftsParamForRestCheck instanceof Map) {
                                        Object countObj = ((Map<?, ?>) maxShiftsParamForRestCheck).get("count");
                                        if (countObj instanceof Number) maxShiftsPerDayForRest = ((Number) countObj).intValue();
                                    }

                                    if (maxShiftsPerDayForRest > 1) {
                                        for (int s2 = 0; s2 < numShifts; s2++) {
                                            if (s1 == s2) continue;
                                            ShiftDefinition shift2Def = shiftDefs.get(s2);
                                            LocalTime shift2Start = convertStringToLocalTime(shift2Def.getStartTime());
                                            if (shift2Start == null) continue;

                                            if (shift2Start.isAfter(shift1End)) {
                                                if (Duration.between(shift1End, shift2Start).toHours() < minRestHours) {
                                                    model.addImplication(works[e][s1][d], works[e][s2][d].not());
                                                }
                                            }
                                        }
                                    }
                                    if (d < numDays - 1) {
                                        for (int sNext = 0; sNext < numShifts; sNext++) {
                                            ShiftDefinition shiftNextDef = shiftDefs.get(sNext);
                                            LocalTime shiftNextStart = convertStringToLocalTime(shiftNextDef.getStartTime());
                                            if (shiftNextStart == null) continue;

                                            long restDurationMillis = Duration.between(shift1End, LocalTime.MAX).toMillis() + 1;
                                            restDurationMillis += Duration.between(LocalTime.MIN, shiftNextStart).toMillis();

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
        // Constraint 8: MAX_WEEKLY_WORK_HOURS (Keep as is)
        if (hardConstraints.containsKey("MAX_WEEKLY_WORK_HOURS")) {
            Object param = hardConstraints.get("MAX_WEEKLY_WORK_HOURS");
            if (param instanceof Map) {
                Object hoursObj = ((Map<?, ?>) param).get("hours");
                if (hoursObj instanceof Number) {
                    long maxWeeklyWorkMillis = ((Number) hoursObj).longValue() * 60 * 60 * 1000;
                    if (maxWeeklyWorkMillis > 0) {
                        for (int e = 0; e < numEmployees; e++) {
                            for (int d = 0; d <= numDays - 7; d++) {
                                List<LinearExpr> weeklyWorkExpressions = new ArrayList<>();
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
                                        } else {
                                            durationMillis = Duration.between(st, LocalTime.MAX).toMillis() + 1 + Duration.between(LocalTime.MIN, et).toMillis();
                                        }
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
        // Constraint 9: NO_CLASHING_SHIFTS_FOR_EMPLOYEE (Keep as is)
        if (Boolean.TRUE.equals(hardConstraints.get("NO_CLASHING_SHIFTS_FOR_EMPLOYEE"))) {
            Object maxShiftsParam = hardConstraints.get("MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE");
            int maxShifts = 1;
            if (maxShiftsParam instanceof Map) {
                Object countObj = ((Map<?, ?>) maxShiftsParam).get("count");
                if (countObj instanceof Number) maxShifts = ((Number) countObj).intValue();
            }
            if (maxShifts > 1) {
                System.out.println("INFO: Applying NO_CLASHING_SHIFTS_FOR_EMPLOYEE with MAX_SHIFTS_PER_DAY > 1. Checking pairwise time overlaps.");
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
                                boolean timeOverlap = false;
                                // Simplified overlap check for same-day context.
                                // Assumes times are for the logical day 'd'.
                                // More robust check would consider how each shift maps to intervals on day 'd'.
                                if (end1.isAfter(start1) || end1.equals(start1)) { // s1 not crossing midnight (or is 24h)
                                    if (end2.isAfter(start2) || end2.equals(start2)) { // s2 not crossing
                                        timeOverlap = start1.isBefore(end2) && end1.isAfter(start2);
                                    } else { // s2 crosses midnight
                                        // s1 vs s2_part1 (start2 to MAX)
                                        timeOverlap = start1.isBefore(LocalTime.MAX) && end1.isAfter(start2);
                                    }
                                } else { // s1 crosses midnight
                                    if (end2.isAfter(start2) || end2.equals(start2)) { // s2 not crossing
                                        // s1_part1 (start1 to MAX) vs s2
                                        timeOverlap = start1.isBefore(end2) && LocalTime.MAX.isAfter(start2);
                                    } else { // both cross midnight - this means they are essentially full-day or overlapping full days
                                        timeOverlap = true; // Simplified: if both are night shifts defined for same day, they likely clash conceptually
                                    }
                                }

                                if (timeOverlap) {
                                    model.addLessOrEqual(LinearExpr.sum(new IntVar[]{works[e][s1_idx][d], works[e][s2_idx][d]}), 1);
                                }
                            }
                        }
                    }
                }
            } else {
                System.out.println("INFO: NO_CLASHING_SHIFTS_FOR_EMPLOYEE is active, but MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE is 1 or not set, so it's implicitly handled.");
            }
        }
        // Constraint 10: MIN_WEEKEND_DAYS_OFF_PER_PERIOD (Keep as is)
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
                        if (numDays >= windowDays) {
                            for (int e = 0; e < numEmployees; e++) {
                                for (int dStartWindow = 0; dStartWindow <= numDays - windowDays; dStartWindow++) {
                                    List<Literal> weekendOffLiteralsInWindow = new ArrayList<>();
                                    for (int dayOffset = 0; dayOffset < windowDays; dayOffset++) {
                                        int currentDayIdx = dStartWindow + dayOffset;
                                        LocalDate currentDate = dateRange.get(currentDayIdx);
                                        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
                                        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                                            weekendOffLiteralsInWindow.add(isWorkingOnDay[e][currentDayIdx].not());
                                        }
                                    }
                                    if (!weekendOffLiteralsInWindow.isEmpty() || minWeekendDaysOff == 0) {
                                        model.addGreaterOrEqual(LinearExpr.sum(weekendOffLiteralsInWindow.toArray(new Literal[0])), minWeekendDaysOff);
                                    }
                                }
                            }
                        } else {
                            System.out.println("WARNING: MIN_WEEKEND_DAYS_OFF_PER_PERIOD: Planning period ("+numDays+" days) is shorter than constraint window ("+windowDays+" days). Constraint skipped.");
                        }
                    }
                }
            }
        }
        // --- END APPLYING CONSTRAINTS ---


        // --- OBJECTIVE: FAIRNESS (Minimize range of total work hours) ---
        IntVar[] employeeTotalWorkMillis = new IntVar[numEmployees];
        long maxPossibleTotalMillisPerDay = 0;
        for(ShiftDefinition sd : shiftDefs){
            LocalTime st = convertStringToLocalTime(sd.getStartTime());
            LocalTime et = convertStringToLocalTime(sd.getEndTime());
            if(st != null && et != null){
                long dur;
                if (et.isAfter(st) || et.equals(st)) dur = Duration.between(st, et).toMillis();
                else dur = Duration.between(st, LocalTime.MAX).toMillis() + 1 + Duration.between(LocalTime.MIN, et).toMillis();
                if(dur > maxPossibleTotalMillisPerDay) maxPossibleTotalMillisPerDay = dur;
            }
        }
        long maxPossibleTotalMillisOverall = maxPossibleTotalMillisPerDay * numDays;


        for (int e = 0; e < numEmployees; e++) {
            List<LinearExpr> workExprForEmployee = new ArrayList<>();
            for (int d = 0; d < numDays; d++) {
                for (int s = 0; s < numShifts; s++) {
                    ShiftDefinition shiftDef = shiftDefs.get(s);
                    LocalTime st = convertStringToLocalTime(shiftDef.getStartTime());
                    LocalTime et = convertStringToLocalTime(shiftDef.getEndTime());
                    if (st == null || et == null) continue;
                    long durationMillis;
                    if (et.isAfter(st) || et.equals(st)) {
                        durationMillis = Duration.between(st, et).toMillis();
                    } else {
                        durationMillis = Duration.between(st, LocalTime.MAX).toMillis() + 1 + Duration.between(LocalTime.MIN, et).toMillis();
                    }
                    workExprForEmployee.add(LinearExpr.term(works[e][s][d], durationMillis));
                }
            }
            employeeTotalWorkMillis[e] = model.newIntVar(0, maxPossibleTotalMillisOverall, "total_work_millis_e" + e);
            if (!workExprForEmployee.isEmpty()) {
                model.addEquality(employeeTotalWorkMillis[e], LinearExpr.sum(workExprForEmployee.toArray(new LinearExpr[0])));
            } else {
                model.addEquality(employeeTotalWorkMillis[e], 0);
            }
        }

        IntVar minTotalMillis = model.newIntVar(0, maxPossibleTotalMillisOverall, "min_total_millis");
        IntVar maxTotalMillis = model.newIntVar(0, maxPossibleTotalMillisOverall, "max_total_millis");

        if (numEmployees > 0) { // Only add min/max equality if there are employees
            model.addMinEquality(minTotalMillis, employeeTotalWorkMillis);
            model.addMaxEquality(maxTotalMillis, employeeTotalWorkMillis);
        } else { // If no employees, min/max are 0
            model.addEquality(minTotalMillis, 0);
            model.addEquality(maxTotalMillis, 0);
        }


        IntVar rangeMillis = model.newIntVar(0, maxPossibleTotalMillisOverall, "range_millis");
        model.addEquality(rangeMillis, LinearExpr.newBuilder().addTerm(maxTotalMillis, 1).addTerm(minTotalMillis, -1).build());
        model.minimize(rangeMillis);

        CpSolver solver = new CpSolver();
        solver.getParameters().setLogSearchProgress(true);
        solver.getParameters().setMaxTimeInSeconds(45.0);

        System.out.println("Starting solver...");
        CpSolverStatus status = solver.solve(model);
        System.out.println("Solver finished with status: " + status);

        List<ScheduledShift> generatedScheduleResult = new ArrayList<>();
        List<RosterStatistics.EmployeeStat> employeeStatsList = new ArrayList<>();
        long overallTotalAssignedShifts = 0;
        double overallTotalAssignedHours = 0;

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            this.lastSolveFeasible = true;
            detailedRosterLogForStats.add("🎉 SOLUTION FOUND! DETAILED ROSTER:"); // Add to stats log
            detailedRosterLogForStats.add("=========================================================");

            for (int d_print = 0; d_print < numDays; d_print++) {
                LocalDate currentDate_print = dateRange.get(d_print);
                String dateLog = String.format("\n📅 DATE: %s (%s)",
                    currentDate_print.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    currentDate_print.getDayOfWeek());
                detailedRosterLogForStats.add(dateLog);
                System.out.print(dateLog);

                boolean dayHasAssignments_print = false;
                for (int s_print = 0; s_print < numShifts; s_print++) {
                    ShiftDefinition shiftDef_print = shiftDefs.get(s_print);
                    List<String> employeesOnThisShiftNames = new ArrayList<>();
                    int assignedCount = 0;
                    for (int e_print = 0; e_print < numEmployees; e_print++) {
                        if (solver.booleanValue(works[e_print][s_print][d_print])) {
                            employeesOnThisShiftNames.add(employees.get(e_print).getFullname() + " (" + employees.get(e_print).getUserLoginId() + ")");
                            assignedCount++;
                        }
                    }
                    if (!employeesOnThisShiftNames.isEmpty()) {
                        String shiftLogHeader = String.format("  🕒 SHIFT: %s (%s - %s) - NV yêu cầu: %d%s, Thực tế: %d",
                            shiftDef_print.getName(), shiftDef_print.getStartTime(), shiftDef_print.getEndTime(),
                            shiftDef_print.getMinEmployees(),
                            (shiftDef_print.getMaxEmployees() != null && shiftDef_print.getMaxEmployees() > 0 ? ("-" + shiftDef_print.getMaxEmployees()) : "+"),
                            assignedCount);
                        detailedRosterLogForStats.add(shiftLogHeader); System.out.println(shiftLogHeader);

                        for (String empName : employeesOnThisShiftNames) {
                            String empLog = "    👤 " + empName;
                            detailedRosterLogForStats.add(empLog); System.out.println(empLog);
                        }
                        dayHasAssignments_print = true;
                    }
                }
                if (!dayHasAssignments_print) {
                    String noAssignmentLog = "  (No assignments for this day)";
                    detailedRosterLogForStats.add(noAssignmentLog); System.out.println(noAssignmentLog);
                }
            }
            detailedRosterLogForStats.add("=========================================================");
            System.out.println("\n=========================================================");


            for (int e = 0; e < numEmployees; e++) {
                long empTotalShifts = 0;
                long empTotalMillis = solver.value(employeeTotalWorkMillis[e]);
                int empNightShifts = 0;
                int empWeekendDaysWorked = 0;
                int currentConsecutiveWorkDays = 0;
                int maxConsecutiveWorkDaysFound = 0;

                for (int d = 0; d < numDays; d++) {
                    boolean workedThisDay = solver.booleanValue(isWorkingOnDay[e][d]);
                    if (workedThisDay) {
                        currentConsecutiveWorkDays++;
                        LocalDate currentDate = dateRange.get(d);
                        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                            empWeekendDaysWorked++;
                        }
                        for (int s = 0; s < numShifts; s++) {
                            if (solver.booleanValue(works[e][s][d])) {
                                empTotalShifts++;
                                if (shiftDefs.get(s).getIsNightShift()) empNightShifts++;
                            }
                        }
                    } else {
                        if (currentConsecutiveWorkDays > maxConsecutiveWorkDaysFound) {
                            maxConsecutiveWorkDaysFound = currentConsecutiveWorkDays;
                        }
                        currentConsecutiveWorkDays = 0;
                    }
                }
                if (currentConsecutiveWorkDays > maxConsecutiveWorkDaysFound) {
                    maxConsecutiveWorkDaysFound = currentConsecutiveWorkDays;
                }

                employeeStatsList.add(RosterStatistics.EmployeeStat.builder()
                    .employeeId(employees.get(e).getUserLoginId())
                    .employeeName(employees.get(e).getFullname())
                    .totalShifts(empTotalShifts)
                    .totalHours(empTotalMillis / (3600000.0))
                    .nightShifts(empNightShifts)
                    .weekendDaysWorked(empWeekendDaysWorked)
                    .maxConsecutiveWorkDays(maxConsecutiveWorkDaysFound)
                    .build());
                overallTotalAssignedShifts += empTotalShifts;
                overallTotalAssignedHours += (empTotalMillis / (3600000.0));
            }

            statsBuilder.totalAssignedShifts(overallTotalAssignedShifts);
            statsBuilder.totalAssignedHours(overallTotalAssignedHours);
            if (numEmployees > 0) {
                statsBuilder.fairness(RosterStatistics.FairnessStats.builder()
                    .minEmployeeHours(solver.value(minTotalMillis) / (3600000.0))
                    .maxEmployeeHours(solver.value(maxTotalMillis) / (3600000.0))
                    .rangeHours(solver.value(rangeMillis) / (3600000.0))
                    .build());
            } else {
                statsBuilder.fairness(RosterStatistics.FairnessStats.builder().minEmployeeHours(0).maxEmployeeHours(0).rangeHours(0).build());
            }
            statsBuilder.employeeStats(employeeStatsList);
            statsBuilder.detailedRosterLog(detailedRosterLogForStats);


            for (int d = 0; d < numDays; d++) {
                LocalDate currentDate = dateRange.get(d);
                for (int e_idx = 0; e_idx < numEmployees; e_idx++) {
                    for (int s = 0; s < numShifts; s++) {
                        if (solver.booleanValue(works[e_idx][s][d])) {
                            StaffModel emp = employees.get(e_idx);
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
                                try {
                                    shiftPort.createShift(shiftToSave);
                                } catch (Exception dbEx) {
                                    System.err.println("Error saving generated shift to DB for " + emp.getUserLoginId() + " on " + currentDate + ": " + dbEx.getMessage());
                                }
                            } else {
                                System.err.println("Could not save shift due to time parsing error for " + shiftDef.getName());
                            }
                            generatedScheduleResult.add(new ScheduledShift(
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
            detailedRosterLogForStats.add("\n❌ NO SOLUTION FOUND or an error occurred. Status: " + status);
            if (status == CpSolverStatus.INFEASIBLE) {
                detailedRosterLogForStats.add("The problem is INFEASIBLE. Consider reviewing constraints, input data, or leave requests.");
            } else if (status == CpSolverStatus.MODEL_INVALID) {
                detailedRosterLogForStats.add("The model is INVALID. There might be an issue with how constraints are defined or with inconsistent data.");
            }
            statsBuilder.detailedRosterLog(detailedRosterLogForStats); // Add logs even if no solution
            statsBuilder.employeeStats(new ArrayList<>()); // Empty list for employee stats
            statsBuilder.fairness(RosterStatistics.FairnessStats.builder().build()); // Empty fairness
        }

        return RosterSolution.builder()
            .scheduledShifts(generatedScheduleResult)
            .statistics(statsBuilder.build())
            .build();
    }

    public static LocalTime convertStringToLocalTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
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