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

    public List<ScheduledShift> solveRoster(RosterRequest request, List<StaffModel> employees, List<AbsenceModel> leaves, List<ShiftModel> existingShiftsDataParam) {
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
            this.lastSolveFeasible = true;
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

        // --- APPLYING CONSTRAINTS (Giữ nguyên các ràng buộc từ 1-10 như bạn đã cung cấp) ---
        // 1. Min/Max employees per shift
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
                                    window.add(isWorkingOnDay[e][d + i]);
                                }
                                model.addLessOrEqual(LinearExpr.sum(window.toArray(new IntVar[0])), maxConsecutive);
                            }
                        }
                    }
                }
            }
        }

        // 6. AVOID_OVERLAPPING_EXISTING_SHIFTS
        // (Sử dụng existingShiftsDataParam được truyền vào)
        if (Boolean.TRUE.equals(hardConstraints.get("AVOID_OVERLAPPING_EXISTING_SHIFTS"))) {
            if (existingShiftsDataParam != null && !existingShiftsDataParam.isEmpty()) {
                System.out.println("INFO: Applying constraint: AVOID_OVERLAPPING_EXISTING_SHIFTS with " + existingShiftsDataParam.size() + " existing shifts.");
                Map<String, Map<LocalDate, List<LocalTime[]>>> employeeDailyIntervals = new HashMap<>();
                for (ShiftModel es : existingShiftsDataParam) { // Sử dụng existingShiftsDataParam ở đây
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

        // 8. MAX_WEEKLY_WORK_HOURS
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

        // 9. NO_CLASHING_SHIFTS_FOR_EMPLOYEE
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
                                // Case 1: Shift 1 không qua đêm
                                if (end1.isAfter(start1) || end1.equals(start1)) {
                                    // Case 1a: Shift 2 không qua đêm
                                    if (end2.isAfter(start2) || end2.equals(start2)) {
                                        timeOverlap = start1.isBefore(end2) && end1.isAfter(start2);
                                    } else { // Case 1b: Shift 2 qua đêm
                                        timeOverlap = (start1.isBefore(LocalTime.MAX) && end1.isAfter(start2)) || // Phần 1 của S2
                                            (start1.isBefore(end2) && end1.isAfter(LocalTime.MIN));   // Phần 2 của S2
                                    }
                                } else { // Case 2: Shift 1 qua đêm
                                    // Case 2a: Shift 2 không qua đêm
                                    if (end2.isAfter(start2) || end2.equals(start2)) {
                                        timeOverlap = (start1.isBefore(end2) && LocalTime.MAX.isAfter(start2)) || // Phần 1 của S1
                                            (LocalTime.MIN.isBefore(end2) && end1.isAfter(start2));   // Phần 2 của S1
                                    } else { // Case 2b: Shift 2 qua đêm (cả hai đều qua đêm - so sánh 2 phần của mỗi ca)
                                        // S1_P1 vs S2_P1 ; S1_P1 vs S2_P2 ; S1_P2 vs S2_P1 ; S1_P2 vs S2_P2
                                        LocalTime s1_p1_start = start1; LocalTime s1_p1_end = LocalTime.MAX;
                                        LocalTime s1_p2_start = LocalTime.MIN; LocalTime s1_p2_end = end1;
                                        LocalTime s2_p1_start = start2; LocalTime s2_p1_end = LocalTime.MAX;
                                        LocalTime s2_p2_start = LocalTime.MIN; LocalTime s2_p2_end = end2;

                                        timeOverlap = (s1_p1_start.isBefore(s2_p1_end) && s1_p1_end.isAfter(s2_p1_start)) ||
                                            (s1_p1_start.isBefore(s2_p2_end) && s1_p1_end.isAfter(s2_p2_start)) ||
                                            (s1_p2_start.isBefore(s2_p1_end) && s1_p2_end.isAfter(s2_p1_start)) ||
                                            (s1_p2_start.isBefore(s2_p2_end) && s1_p2_end.isAfter(s2_p2_start));
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
                System.out.println("INFO: NO_CLASHING_SHIFTS_FOR_EMPLOYEE is active, but MAX_SHIFTS_PER_DAY_FOR_EMPLOYEE is 1 or not set, so it's implicitly handled by limiting to 1 shift/day.");
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
                                    if (!weekendOffLiteralsInWindow.isEmpty() || minWeekendDaysOff == 0) { // Nếu minWeekendDaysOff = 0, ràng buộc vẫn cần được thêm (sum >= 0)
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
        long maxPossibleTotalMillis = 0; // Tính tổng thời gian tối đa có thể có để đặt cận trên
        for (ShiftDefinition sd : shiftDefs) {
            LocalTime st = convertStringToLocalTime(sd.getStartTime());
            LocalTime et = convertStringToLocalTime(sd.getEndTime());
            if(st != null && et != null) {
                long dur;
                if (et.isAfter(st) || et.equals(st)) dur = Duration.between(st, et).toMillis();
                else dur = Duration.between(st, LocalTime.MAX).toMillis() + 1 + Duration.between(LocalTime.MIN, et).toMillis();
                if (dur > maxPossibleTotalMillis) maxPossibleTotalMillis = dur;
            }
        }
        maxPossibleTotalMillis *= numDays; // Giả sử một người có thể làm ca dài nhất mỗi ngày

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
            employeeTotalWorkMillis[e] = model.newIntVar(0, maxPossibleTotalMillis, "total_work_millis_e" + e);
            if (!workExprForEmployee.isEmpty()) {
                model.addEquality(employeeTotalWorkMillis[e], LinearExpr.sum(workExprForEmployee.toArray(new LinearExpr[0])));
            } else {
                model.addEquality(employeeTotalWorkMillis[e], 0); // Nếu không có ca nào, tổng giờ làm là 0
            }
        }

        IntVar minTotalMillis = model.newIntVar(0, maxPossibleTotalMillis, "min_total_millis");
        IntVar maxTotalMillis = model.newIntVar(0, maxPossibleTotalMillis, "max_total_millis");

        model.addMinEquality(minTotalMillis, employeeTotalWorkMillis);
        model.addMaxEquality(maxTotalMillis, employeeTotalWorkMillis);

        IntVar rangeMillis = model.newIntVar(0, maxPossibleTotalMillis, "range_millis");
        model.addEquality(rangeMillis, LinearExpr.newBuilder().addTerm(maxTotalMillis, 1).addTerm(minTotalMillis, -1).build());
        model.minimize(rangeMillis);


        // --- SOLVER ---
        CpSolver solver = new CpSolver();
        solver.getParameters().setLogSearchProgress(true);
        solver.getParameters().setEnumerateAllSolutions(false); // Tìm một giải pháp tốt thay vì tất cả
        // solver.getParameters().setOptimizeWithCore(true); // Thử nghiệm
        // solver.getParameters().setSearchBranching(SatParameters.VariableSelectionStrategy.CHOOSE_FIRST, SatParameters.DomainReductionStrategy.SELECT_MIN_VALUE);

        solver.getParameters().setMaxTimeInSeconds(45.0); // Tăng thời gian nếu cần

        System.out.println("Starting solver...");
        CpSolverStatus status = solver.solve(model);
        System.out.println("Solver finished with status: " + status);

        List<ScheduledShift> generatedSchedule = new ArrayList<>();
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            this.lastSolveFeasible = true;
            System.out.println("\n🎉 SOLUTION FOUND! DETAILED ROSTER:");
            System.out.println("=========================================================");
            // In lịch chi tiết (như cũ)
            for (int d_print = 0; d_print < numDays; d_print++) {
                LocalDate currentDate_print = dateRange.get(d_print);
                System.out.println("\n📅 DATE: " + currentDate_print.format(DateTimeFormatter.ISO_LOCAL_DATE) + " (" + currentDate_print.getDayOfWeek() + ")");
                boolean dayHasAssignments_print = false;
                for (int s_print = 0; s_print < numShifts; s_print++) {
                    ShiftDefinition shiftDef_print = shiftDefs.get(s_print);
                    List<String> employeesOnThisShift = new ArrayList<>();
                    for (int e_print = 0; e_print < numEmployees; e_print++) {
                        if (solver.booleanValue(works[e_print][s_print][d_print])) {
                            employeesOnThisShift.add(employees.get(e_print).getFullname() + " (" + employees.get(e_print).getUserLoginId() + ")");
                        }
                    }
                    if (!employeesOnThisShift.isEmpty()) {
                        System.out.println("  🕒 SHIFT: " + shiftDef_print.getName() + " (" + shiftDef_print.getStartTime() + " - " + shiftDef_print.getEndTime() + ") - NV yêu cầu: " + shiftDef_print.getMinEmployees() + (shiftDef_print.getMaxEmployees()!=null && shiftDef_print.getMaxEmployees()>0 ? ("-"+shiftDef_print.getMaxEmployees()) : "+") + ", Thực tế: " + employeesOnThisShift.size());
                        for (String empName : employeesOnThisShift) { System.out.println("    👤 " + empName); }
                        dayHasAssignments_print = true;
                    }
                }
                if (!dayHasAssignments_print) System.out.println("  (No assignments for this day)");
            }
            System.out.println("=========================================================");

            // --- THỐNG KÊ CHI TIẾT ---
            System.out.println("\n📊 DETAILED STATISTICS:");
            System.out.println("---------------------------------------------------------");
            long totalAssignedShifts = 0;
            long totalAssignedHoursMillis = 0;

            for (int e = 0; e < numEmployees; e++) {
                long empTotalShifts = 0;
                long empTotalMillis = solver.value(employeeTotalWorkMillis[e]);
                int empNightShifts = 0;
                int empWeekendDaysWorked = 0;
                int currentConsecutiveWorkDays = 0;
                int maxConsecutiveWorkDaysFound = 0;

                for (int d = 0; d < numDays; d++) {
                    boolean workedThisDay = false;
                    for (int s = 0; s < numShifts; s++) {
                        if (solver.booleanValue(works[e][s][d])) {
                            empTotalShifts++;
                            workedThisDay = true;
                            if (shiftDefs.get(s).getIsNightShift()) empNightShifts++;
                        }
                    }
                    if (workedThisDay) {
                        currentConsecutiveWorkDays++;
                        LocalDate currentDate = dateRange.get(d);
                        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                            empWeekendDaysWorked++;
                        }
                    } else {
                        if (currentConsecutiveWorkDays > maxConsecutiveWorkDaysFound) {
                            maxConsecutiveWorkDaysFound = currentConsecutiveWorkDays;
                        }
                        currentConsecutiveWorkDays = 0;
                    }
                }
                if (currentConsecutiveWorkDays > maxConsecutiveWorkDaysFound) { // Kiểm tra lần cuối
                    maxConsecutiveWorkDaysFound = currentConsecutiveWorkDays;
                }

                System.out.printf("  👤 Employee: %-25s (ID: %s)%n", employees.get(e).getFullname(), employees.get(e).getUserLoginId());
                System.out.printf("    Total Shifts: %d, Total Hours: %.2f%n", empTotalShifts, empTotalMillis / (3600000.0));
                System.out.printf("    Night Shifts: %d, Weekend Days Worked: %d, Max Consecutive Work Days: %d%n", empNightShifts, empWeekendDaysWorked, maxConsecutiveWorkDaysFound);
                totalAssignedShifts += empTotalShifts;
                totalAssignedHoursMillis += empTotalMillis;
            }
            System.out.println("---------------------------------------------------------");
            System.out.printf("  OVERALL: Total Assigned Shifts: %d, Total Assigned Hours: %.2f%n", totalAssignedShifts, totalAssignedHoursMillis / (3600000.0));
            System.out.printf("  FAIRNESS: Min Employee Hours: %.2f, Max Employee Hours: %.2f, Range: %.2f hours%n",
                solver.value(minTotalMillis) / (3600000.0),
                solver.value(maxTotalMillis) / (3600000.0),
                solver.value(rangeMillis) / (3600000.0));
            System.out.println("=========================================================");


            // Populate and save shifts (như cũ)
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
                System.out.println("The problem is INFEASIBLE. Consider reviewing constraints, input data, or leave requests.");
            } else if (status == CpSolverStatus.MODEL_INVALID) {
                System.out.println("The model is INVALID. There might be an issue with how constraints are defined or with inconsistent data.");
            }
        }
        return generatedSchedule;
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
