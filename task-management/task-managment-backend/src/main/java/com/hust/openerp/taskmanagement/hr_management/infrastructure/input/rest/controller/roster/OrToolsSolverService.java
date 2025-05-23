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
import java.util.UUID; // Import UUID
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

    public RosterSolution solveRoster(RosterRequest request, List<StaffModel> employees, List<AbsenceModel> leaves, List<ShiftModel> existingShiftsDataParam) {
        this.lastSolveFeasible = false;
        CpModel model = new CpModel();

        List<ShiftDefinition> shiftDefs = request.getDefinedShifts();
        Map<String, Object> hardConstraints = request.getActiveHardConstraints();

        LocalDate rosterPeriodStartDate = request.getStartDate(); // Use request's start and end date for stats
        LocalDate rosterPeriodEndDate = request.getEndDate();
        List<LocalDate> dateRange = rosterPeriodStartDate.datesUntil(rosterPeriodEndDate.plusDays(1)).collect(Collectors.toList());
        int numDays = dateRange.size();
        int numEmployees = employees.size();
        int numShifts = shiftDefs.size();

        RosterStatistics.RosterStatisticsBuilder statsBuilder = RosterStatistics.builder()
            .rosterStartDate(rosterPeriodStartDate)
            .rosterEndDate(rosterPeriodEndDate);
        List<String> detailedRosterLogForStats = new ArrayList<>();
        List<UUID> createdShiftIds = new ArrayList<>();


        if (numEmployees == 0 || numShifts == 0 || numDays == 0) {
            System.out.println("Không có nhân viên, ca làm việc hoặc ngày để xếp lịch. Trả về lịch rỗng.");
            this.lastSolveFeasible = true;
            statsBuilder.employeeStats(new ArrayList<>())
                .detailedRosterLog(List.of("Không có nhân viên, ca làm việc hoặc ngày để xếp lịch."))
                .fairnessHours(RosterStatistics.FairnessStats.builder().build())
                .fairnessNightShifts(RosterStatistics.FairnessCountStats.builder().build())
                .fairnessSundayShifts(RosterStatistics.FairnessCountStats.builder().build());
            return RosterSolution.builder()
                .scheduledShifts(new ArrayList<>())
                .statistics(statsBuilder.build())
                .createdShiftIds(createdShiftIds)
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

        // --- APPLYING CONSTRAINTS (1-10, same logic as before) ---
        // ... (Constraints 1-5, 7-10 are assumed to be here and correct from your previous version)
        // Please ensure constraints 1-5 and 7-10 are correctly placed here.
        // I will re-paste constraint 6, 9 for context.
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
        // Constraint 3: ENSURE_EMPLOYEE_APPROVED_LEAVE
        if (Boolean.TRUE.equals(hardConstraints.get("ENSURE_EMPLOYEE_APPROVED_LEAVE"))) {
            for (AbsenceModel leave : leaves) {
                Integer empIdx = employeeIndexMap.get(leave.getUserId());
                if (empIdx != null) {
                    try {
                        LocalDate leaveDate = leave.getDate();
                        if (!leaveDate.isBefore(rosterPeriodStartDate) && !leaveDate.isAfter(rosterPeriodEndDate)) { // Use rosterPeriod dates
                            int dayIdx = (int) ChronoUnit.DAYS.between(rosterPeriodStartDate, leaveDate);
                            if (dayIdx >= 0 && dayIdx < numDays) {
                                for (int s = 0; s < numShifts; s++) {
                                    model.addEquality(works[empIdx][s][dayIdx], 0);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing leave date for ENSURE_EMPLOYEE_APPROVED_LEAVE: " + leave.getUserId() + ". Error: " + e.getMessage());
                    }
                }
            }
        }

        // Constraint 4: NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT
        if (Boolean.TRUE.equals(hardConstraints.get("NO_WORK_NEXT_DAY_AFTER_NIGHT_SHIFT"))) {
            for (int e = 0; e < numEmployees; e++) {
                for (int d = 0; d < numDays - 1; d++) {
                    for (int sNight = 0; sNight < numShifts; sNight++) {
                        if (shiftDefs.get(sNight).getIsNightShift()) {
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
        // Constraint 5: MAX_CONSECUTIVE_WORK_DAYS
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
                                    window.add(isWorkingOnDay[e][d + i]);
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
            // Logic from your previous code for this constraint
            if (existingShiftsDataParam != null && !existingShiftsDataParam.isEmpty()) {
                // ... (full logic as provided in previous response)
                Map<String, Map<LocalDate, List<LocalTime[]>>> employeeDailyIntervals = new HashMap<>();
                for (ShiftModel es : existingShiftsDataParam) {
                    String userId = es.getUserId(); LocalDate esDate = es.getDate();
                    LocalTime esStartTime = es.getStartTime(); LocalTime esEndTime = es.getEndTime();
                    if (userId == null || esDate == null || esStartTime == null || esEndTime == null) continue;
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
                            } catch (DateTimeParseException ex) { continue; }
                            if (existingIntervalsOnCurrentDay != null) {
                                LocalTime effectiveNewEnd = (newShiftEndTime.isAfter(newShiftStartTime) || newShiftEndTime.equals(newShiftStartTime)) ? newShiftEndTime : LocalTime.MAX;
                                for (LocalTime[] existingInt : existingIntervalsOnCurrentDay) {
                                    if (newShiftStartTime.isBefore(existingInt[1]) && effectiveNewEnd.isAfter(existingInt[0])) {
                                        model.addEquality(works[e][s][d], 0); overlapDetectedThisIteration = true; break;
                                    }
                                }
                            }
                            if (overlapDetectedThisIteration) continue;
                            if (newShiftEndTime.isBefore(newShiftStartTime)) {
                                LocalDate nextDayDate = currentDate.plusDays(1);
                                int nextDayIdx = (int) ChronoUnit.DAYS.between(rosterPeriodStartDate, nextDayDate);
                                if (nextDayIdx >= 0 && nextDayIdx < numDays) {
                                    List<LocalTime[]> existingIntervalsOnNextDay = existingIntervalsForEmp.get(nextDayDate);
                                    if (existingIntervalsOnNextDay != null) {
                                        LocalTime part2Start = LocalTime.MIN; LocalTime part2End = newShiftEndTime;
                                        for (LocalTime[] existingIntNext : existingIntervalsOnNextDay) {
                                            if (part2Start.isBefore(existingIntNext[1]) && part2End.isAfter(existingIntNext[0])) {
                                                model.addEquality(works[e][s][d], 0); break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else { System.out.println("INFO: AVOID_OVERLAPPING_EXISTING_SHIFTS is active, but no existing shifts data passed to check against.");}
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
                                            if ((restDurationMillis / (3600000.0)) < minRestHours) { // 3600000.0 = 1000 * 60 * 60
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
                    long maxWeeklyWorkMillis = ((Number) hoursObj).longValue() * 3600000L; // hours to millis
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
                                        long durationMillis = Duration.between(st, et).toMillis();
                                        if (durationMillis < 0) { // Crosses midnight
                                            durationMillis += Duration.ofDays(1).toMillis();
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
                                // Simplified overlap: (StartA < EndB) and (EndA > StartB)
                                // This simple check doesn't fully handle midnight crossing shifts for pairwise on same day.
                                // For this to be robust with midnight shifts, intervals need to be split or handled carefully.
                                boolean timeOverlap = start1.isBefore(end2) && end1.isAfter(start2);
                                if (end1.isBefore(start1)) { // def1 crosses midnight
                                    timeOverlap = timeOverlap || (start1.isBefore(end2) || end1.isAfter(start2)); // Looser check if one crosses
                                }
                                if (end2.isBefore(start2)) { // def2 crosses midnight
                                    timeOverlap = timeOverlap || (start2.isBefore(end1) || end2.isAfter(start1)); // Looser check if one crosses
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

        // --- OBJECTIVE: FAIRNESS (Minimize range of total work hours) ---
        IntVar[] employeeTotalWorkMillis = new IntVar[numEmployees];
        long maxPossibleTotalMillisPerDay = 0;
        for(ShiftDefinition sd : shiftDefs){
            LocalTime st = convertStringToLocalTime(sd.getStartTime());
            LocalTime et = convertStringToLocalTime(sd.getEndTime());
            if(st != null && et != null){
                long dur = Duration.between(st, et).toMillis();
                if(dur < 0) dur += Duration.ofDays(1).toMillis(); // Add 24h if crosses midnight
                if(dur > maxPossibleTotalMillisPerDay) maxPossibleTotalMillisPerDay = dur;
            }
        }
        long maxPossibleTotalMillisOverall = maxPossibleTotalMillisPerDay * numDays;
        if (maxPossibleTotalMillisOverall == 0 && numDays > 0 && numShifts > 0) { // Avoid 0 upper bound if shifts exist
            maxPossibleTotalMillisOverall = Duration.ofDays(1).toMillis() * numDays; // Default to 24h * numDays
        }


        for (int e = 0; e < numEmployees; e++) {
            List<LinearExpr> workExprForEmployee = new ArrayList<>();
            for (int d = 0; d < numDays; d++) {
                for (int s = 0; s < numShifts; s++) {
                    ShiftDefinition shiftDef = shiftDefs.get(s);
                    LocalTime st = convertStringToLocalTime(shiftDef.getStartTime());
                    LocalTime et = convertStringToLocalTime(shiftDef.getEndTime());
                    if (st == null || et == null) continue;
                    long durationMillis = Duration.between(st,et).toMillis();
                    if(durationMillis < 0) durationMillis += Duration.ofDays(1).toMillis(); // Add 24h if crosses midnight
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

        if (numEmployees > 0) {
            model.addMinEquality(minTotalMillis, employeeTotalWorkMillis);
            model.addMaxEquality(maxTotalMillis, employeeTotalWorkMillis);
        } else {
            model.addEquality(minTotalMillis, 0);
            model.addEquality(maxTotalMillis, 0);
        }

        IntVar rangeMillis = model.newIntVar(0, maxPossibleTotalMillisOverall, "range_millis");
        model.addEquality(rangeMillis, LinearExpr.newBuilder().addTerm(maxTotalMillis, 1).addTerm(minTotalMillis, -1).build());
        model.minimize(rangeMillis);


        // Additional fairness metrics (counts) - these are for reporting, not direct objectives here
        IntVar[] employeeNightShiftsCount = new IntVar[numEmployees];
        IntVar[] employeeSundayShiftsCount = new IntVar[numEmployees]; // Can add Saturday similarly if needed

        for (int e = 0; e < numEmployees; e++) {
            List<BoolVar> nightShiftsForEmp = new ArrayList<>();
            List<BoolVar> sundayShiftsForEmp = new ArrayList<>();
            for (int d = 0; d < numDays; d++) {
                for (int s = 0; s < numShifts; s++) {
                    if (shiftDefs.get(s).getIsNightShift()) {
                        nightShiftsForEmp.add(works[e][s][d]);
                    }
                    if (dateRange.get(d).getDayOfWeek() == DayOfWeek.SUNDAY) {
                        sundayShiftsForEmp.add(works[e][s][d]);
                    }
                }
            }
            employeeNightShiftsCount[e] = model.newIntVar(0, numDays * numShifts, "night_shifts_e" + e);
            model.addEquality(employeeNightShiftsCount[e], LinearExpr.sum(nightShiftsForEmp.toArray(new BoolVar[0])));

            employeeSundayShiftsCount[e] = model.newIntVar(0, numDays, "sunday_shifts_e" + e); // Max 1 shift per sunday for count
            List<BoolVar> worksOnSundayVars = new ArrayList<>();
            for (int d=0; d<numDays; d++) {
                if (dateRange.get(d).getDayOfWeek() == DayOfWeek.SUNDAY) {
                    worksOnSundayVars.add(isWorkingOnDay[e][d]);
                }
            }
            if (!worksOnSundayVars.isEmpty()) {
                model.addEquality(employeeSundayShiftsCount[e], LinearExpr.sum(worksOnSundayVars.toArray(new BoolVar[0])));
            } else {
                model.addEquality(employeeSundayShiftsCount[e], 0);
            }
        }


        CpSolver solver = new CpSolver();
        solver.getParameters().setLogSearchProgress(true);
        solver.getParameters().setMaxTimeInSeconds(60.0); // Increased time

        System.out.println("Starting solver...");
        CpSolverStatus status = solver.solve(model);
        System.out.println("Solver finished with status: " + status);

        List<ScheduledShift> generatedScheduleResult = new ArrayList<>();
        List<RosterStatistics.EmployeeStat> employeeStatsList = new ArrayList<>();
        long overallTotalAssignedShifts = 0;
        double overallTotalAssignedHours = 0;

        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            this.lastSolveFeasible = true;
            // ... (Detailed roster logging to detailedRosterLogForStats - same as previous version) ...
            detailedRosterLogForStats.add("🎉 SOLUTION FOUND! DETAILED ROSTER:");
            detailedRosterLogForStats.add("=========================================================");
            for (int d_print = 0; d_print < numDays; d_print++) {
                LocalDate currentDate_print = dateRange.get(d_print);
                String dateLog = String.format("\n📅 DATE: %s (%s)", currentDate_print.format(DateTimeFormatter.ISO_LOCAL_DATE), currentDate_print.getDayOfWeek());
                detailedRosterLogForStats.add(dateLog); System.out.println(dateLog);
                boolean dayHasAssignments_print = false;
                for (int s_print = 0; s_print < numShifts; s_print++) {
                    ShiftDefinition shiftDef_print = shiftDefs.get(s_print);
                    List<String> employeesOnThisShiftNames = new ArrayList<>(); int assignedCount = 0;
                    for (int e_print = 0; e_print < numEmployees; e_print++) {
                        if (solver.booleanValue(works[e_print][s_print][d_print])) {
                            employeesOnThisShiftNames.add(employees.get(e_print).getFullname() + " (" + employees.get(e_print).getStaffCode() + ")"); // Use staffCode
                            assignedCount++;
                        }
                    }
                    if (!employeesOnThisShiftNames.isEmpty()) {
                        String shiftLogHeader = String.format("  🕒 SHIFT: %s (%s - %s) - NV yêu cầu: %d%s, Thực tế: %d", shiftDef_print.getName(), shiftDef_print.getStartTime(), shiftDef_print.getEndTime(), shiftDef_print.getMinEmployees(), (shiftDef_print.getMaxEmployees()!=null && shiftDef_print.getMaxEmployees()>0 ? ("-"+shiftDef_print.getMaxEmployees()) : "+"), assignedCount);
                        detailedRosterLogForStats.add(shiftLogHeader); System.out.println(shiftLogHeader);
                        for (String empName : employeesOnThisShiftNames) { String empLog = "    👤 " + empName; detailedRosterLogForStats.add(empLog); System.out.println(empLog); }
                        dayHasAssignments_print = true;
                    }
                }
                if (!dayHasAssignments_print) { String noAssignmentLog = "  (No assignments for this day)"; detailedRosterLogForStats.add(noAssignmentLog); System.out.println(noAssignmentLog); }
            }
            detailedRosterLogForStats.add("=========================================================");
            System.out.println("\n=========================================================");


            long minEmpNightShifts = numDays * numShifts + 1, maxEmpNightShifts = -1;
            long minEmpSundayShifts = numDays + 1, maxEmpSundayShifts = -1;

            for (int e = 0; e < numEmployees; e++) {
                long empTotalShifts = 0;
                long empTotalMillis = solver.value(employeeTotalWorkMillis[e]);
                int empNightShiftsVal = (int) solver.value(employeeNightShiftsCount[e]);
                int empSundayShiftsVal = (int) solver.value(employeeSundayShiftsCount[e]);
                int empSaturdayShiftsVal = 0; // Calculate Saturday shifts
                int currentConsecutiveWorkDays = 0;
                int maxConsecutiveWorkDaysFound = 0;

                for (int d = 0; d < numDays; d++) {
                    boolean workedThisDay = solver.booleanValue(isWorkingOnDay[e][d]);
                    if (workedThisDay) {
                        currentConsecutiveWorkDays++;
                        LocalDate currentDate = dateRange.get(d);
                        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                            for (int s = 0; s < numShifts; s++) { if(solver.booleanValue(works[e][s][d])) empSaturdayShiftsVal++;}
                        }
                        for (int s = 0; s < numShifts; s++) {
                            if (solver.booleanValue(works[e][s][d])) empTotalShifts++;
                        }
                    } else {
                        if (currentConsecutiveWorkDays > maxConsecutiveWorkDaysFound) maxConsecutiveWorkDaysFound = currentConsecutiveWorkDays;
                        currentConsecutiveWorkDays = 0;
                    }
                }
                if (currentConsecutiveWorkDays > maxConsecutiveWorkDaysFound) maxConsecutiveWorkDaysFound = currentConsecutiveWorkDays;

                minEmpNightShifts = Math.min(minEmpNightShifts, empNightShiftsVal);
                maxEmpNightShifts = Math.max(maxEmpNightShifts, empNightShiftsVal);
                minEmpSundayShifts = Math.min(minEmpSundayShifts, empSundayShiftsVal);
                maxEmpSundayShifts = Math.max(maxEmpSundayShifts, empSundayShiftsVal);

                employeeStatsList.add(RosterStatistics.EmployeeStat.builder()
                    .staffCode(employees.get(e).getStaffCode()) // Use staffCode
                    .employeeName(employees.get(e).getFullname())
                    .totalShifts(empTotalShifts)
                    .totalHours(empTotalMillis / (3600000.0))
                    .nightShifts(empNightShiftsVal)
                    .sundayShiftsWorked(empSundayShiftsVal)
                    .saturdayShiftsWorked(empSaturdayShiftsVal)
                    .maxConsecutiveWorkDays(maxConsecutiveWorkDaysFound)
                    .build());
                overallTotalAssignedShifts += empTotalShifts;
                overallTotalAssignedHours += (empTotalMillis / (3600000.0));
            }

            statsBuilder.totalAssignedShifts(overallTotalAssignedShifts);
            statsBuilder.totalAssignedHours(overallTotalAssignedHours);
            if (numEmployees > 0) {
                statsBuilder.fairnessHours(RosterStatistics.FairnessStats.builder()
                    .minEmployeeValue(solver.value(minTotalMillis) / (3600000.0))
                    .maxEmployeeValue(solver.value(maxTotalMillis) / (3600000.0))
                    .rangeValue(solver.value(rangeMillis) / (3600000.0))
                    .build());
                statsBuilder.fairnessNightShifts(RosterStatistics.FairnessCountStats.builder()
                    .minEmployeeCount(minEmpNightShifts > maxEmpNightShifts ? 0 : minEmpNightShifts) // Handle if no night shifts
                    .maxEmployeeCount(maxEmpNightShifts < 0 ? 0 : maxEmpNightShifts)
                    .rangeCount(maxEmpNightShifts < minEmpNightShifts ? 0 : maxEmpNightShifts - minEmpNightShifts)
                    .build());
                statsBuilder.fairnessSundayShifts(RosterStatistics.FairnessCountStats.builder()
                    .minEmployeeCount(minEmpSundayShifts > maxEmpSundayShifts ? 0 : minEmpSundayShifts) // Handle if no Sunday shifts
                    .maxEmployeeCount(maxEmpSundayShifts < 0 ? 0 : maxEmpSundayShifts)
                    .rangeCount(maxEmpSundayShifts < minEmpSundayShifts ? 0 : maxEmpSundayShifts - minEmpSundayShifts)
                    .build());
            } else {
                statsBuilder.fairnessHours(RosterStatistics.FairnessStats.builder().minEmployeeValue(0).maxEmployeeValue(0).rangeValue(0).build());
                statsBuilder.fairnessNightShifts(RosterStatistics.FairnessCountStats.builder().build());
                statsBuilder.fairnessSundayShifts(RosterStatistics.FairnessCountStats.builder().build());
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
                                    .userId(emp.getUserLoginId()) // Still use userId for saving if ShiftModel expects it
                                    .note(shiftDef.getName())
                                    .date(currentDate)
                                    .startTime(localStartTime)
                                    .endTime(localEndTime)
                                    .build();
                                try {
                                    ShiftModel savedShift = shiftPort.createShift(shiftToSave); // Capture returned model
                                    if (savedShift != null && savedShift.getId() != null) {
                                        createdShiftIds.add(savedShift.getId());
                                    }
                                } catch (Exception dbEx) {
                                    System.err.println("Error saving generated shift to DB for " + emp.getUserLoginId() + " on " + currentDate + ": " + dbEx.getMessage());
                                }
                            } else {
                                System.err.println("Could not save shift due to time parsing error for " + shiftDef.getName());
                            }
                            generatedScheduleResult.add(new ScheduledShift(
                                emp.getUserLoginId(), // For ScheduledShift DTO, can keep userLoginId or change to staffCode
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
            statsBuilder.detailedRosterLog(detailedRosterLogForStats);
            statsBuilder.employeeStats(new ArrayList<>());
            statsBuilder.fairnessHours(RosterStatistics.FairnessStats.builder().build());
            statsBuilder.fairnessNightShifts(RosterStatistics.FairnessCountStats.builder().build());
            statsBuilder.fairnessSundayShifts(RosterStatistics.FairnessCountStats.builder().build());
        }

        return RosterSolution.builder()
            .scheduledShifts(generatedScheduleResult)
            .statistics(statsBuilder.build())
            .createdShiftIds(createdShiftIds) // Add the list of created shift IDs
            .build();
    }

    public static LocalTime convertStringToLocalTime(String timeString) {
        if (timeString == null || timeString.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(timeString);
        } catch (DateTimeParseException e) {
            // System.err.println("Error converting string '" + timeString + "' to LocalTime. Expected format HH:mm. Error: " + e.getMessage());
            return null; // Reduced verbosity for cleaner logs, error is usually evident from context
        }
    }
}