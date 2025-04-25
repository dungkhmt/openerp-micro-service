package openerp.openerpresourceserver.service.impl;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.request.attendance.SyncAttendanceRequest;
import openerp.openerpresourceserver.entity.Attendance;
import openerp.openerpresourceserver.entity.AttendanceRange;
import openerp.openerpresourceserver.entity.AttendanceReport;
import openerp.openerpresourceserver.enums.AttendanceStatusEnum;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.repo.AttendanceRangeRepository;
import openerp.openerpresourceserver.repo.AttendanceReportRepository;
import openerp.openerpresourceserver.repo.AttendanceRepository;
import openerp.openerpresourceserver.repo.EmployeeRepository;
import openerp.openerpresourceserver.repo.custom.SyncAttendanceRepository;
import openerp.openerpresourceserver.repo.projection.AttendanceDTO;
import openerp.openerpresourceserver.service.SyncAttendanceService;
import openerp.openerpresourceserver.util.DateUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

import static openerp.openerpresourceserver.util.DateUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncAttendanceServiceImpl implements SyncAttendanceService {
    private final AttendanceReportRepository attendanceReportRepository;
    private final SyncAttendanceRepository syncAttendanceRepository;
    private final AttendanceRangeRepository attendanceRangeRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalTime startLunch;
    private LocalTime endLunch;
    private int breakTimeInMinutes;
    private Double fullHours;

    @Override
    public void syncAttendanceByRange(LocalDate startDate, LocalDate endDate, List<Integer> employeeIds, Boolean overwrite) {
        syncAttendance(
                DateUtil.convertLocalDateToInteger(startDate),
                DateUtil.convertLocalDateToInteger(endDate),
                employeeIds,
                overwrite);
    }

    @Override
    public void syncAttendanceRealTime(SyncAttendanceRequest request) {
        // sync attendance
        syncAttendance(request.getDate(), request.getDate(), List.of(request.getId()), false);
    }

    @Override
    public Double computeAttendanceTime(LocalTime clockIn, LocalTime clockOut, AttendanceRange attendanceRange) {
        setUpTime(attendanceRange);
        return computeAttendanceTime(clockIn, clockOut);
    }

    private Double computeAttendanceTime(LocalTime clockIn, LocalTime clockOut) {
        // If no end time then attendance time would be 0
        if (clockIn.equals(clockOut)) return 0.0;

        // If the check-in time is out of check out end time then att time is 0
        if (clockIn.isAfter(endTime)) {
            return 0.0;
        }
        clockIn = adjustClockIn(clockIn, startTime);
        clockOut = adjustClockOut(clockOut, endTime);

        // If the attendance check-in and out time is both in lunch break then attendance time is 0
        boolean isStartTimeInLunchBreak = isAttendanceCheckTimeInLunchBreak(clockIn);
        boolean isEndTimeInLunchBreak = isAttendanceCheckTimeInLunchBreak(clockOut);

        if (isStartTimeInLunchBreak && isEndTimeInLunchBreak) {
            return 0.0;
        }

        // Adjust start time if it falls during the lunch break
        if (isStartTimeInLunchBreak) clockIn = endLunch;

        // Adjust end time if it falls during the lunch break
        if (isEndTimeInLunchBreak) clockOut = startLunch;

        // Check if the attendance period crosses the lunch break
        int breakTime = (clockIn.isBefore(startLunch) && clockOut.isAfter(endLunch))
                ? breakTimeInMinutes : 0;

        // Compute attendance time in minutes and subtract break time
        double attendanceTime = (ChronoUnit.MINUTES.between(clockIn.withSecond(0), clockOut.withSecond(0)) - breakTime) / 60.0;

        // Round to 1 place
        return Math.max(0.0, Math.min(fullHours, Math.round(attendanceTime * 10.0) / 10.0));
    }

    private void syncAttendance(Integer startDate, Integer endDate, List<Integer> employeeIds, Boolean overwrite) {
        List<AttendanceRange> attendanceRanges = attendanceRangeRepository.findAll();
        List<AttendanceDTO> attendances = syncAttendanceRepository.findAttendancesWithFilters(startDate, endDate, employeeIds);
        List<AttendanceReport> attendanceReports = syncAttendanceRepository
                .findAttendanceReportsWithFilters(startDate, endDate, employeeIds);

        // Convert list of attendance reports to map for easier look up
        Map<AttendanceMapKey, AttendanceReport> attendanceReportMap = getAttendaceReportMap(attendanceReports);
        Map<Long, AttendanceRange> attendanceRangeMap =
                attendanceRanges
                        .stream()
                        .collect(Collectors.toMap(AttendanceRange::getId, range -> range));

        for (AttendanceDTO attendance : attendances) {
            int employeeId = attendance.getId();
            AttendanceRange attendanceRange = attendanceRangeMap.get(attendance.getAttendanceRangeId());
            Double attendanceTime;
            AttendanceStatusEnum attendanceStatus;
            AttendanceReport attendanceReport = attendanceReportMap
                    .get(
                            AttendanceMapKey.builder()
                                    .employeeId(attendance.getId())
                                    .date(attendance.getDate())
                                    .build()
                    );
            setUpTime(attendanceRange);

            attendanceTime = computeAttendanceTime(
                    attendance.getClockIn().toLocalTime(),
                    attendance.getClockOut().toLocalTime()
            );

            if (attendanceTime.equals(fullHours)) {
                attendanceStatus = AttendanceStatusEnum.FULL;
            } else {
                attendanceStatus = AttendanceStatusEnum.determineStatus(
                        DateUtil.getLocalTime(attendance.getClockIn()),
                        DateUtil.getLocalTime(attendance.getClockOut()),
                        startTime,
                        endTime);
            }

            // if the report is null mean the report has not been inserted
            if (attendanceReport == null) {
                attendanceReport = AttendanceReport
                        .builder()
                        .employeeId(employeeId)
                        .date(attendance.getDate())
                        .rawStartTime(attendance.getClockIn())
                        .rawEndTime(attendance.getClockOut())
                        .rawAttendanceTime(attendanceTime)
                        .startTime(attendance.getClockIn())
                        .endTime(attendance.getClockOut())
                        .attendanceTime(attendanceTime)
                        .leaveTime(0.0)
                        .attendanceRangeId(attendance.getAttendanceRangeId())
                        .status(attendanceStatus.ordinal())
                        .build();
                attendanceReports.add(attendanceReport);
            }
            // if the report != null, we just need to update the end time and recalculate attendance time
            else {
                // edit time
                attendanceReport.setRawStartTime(attendance.getClockIn());
                attendanceReport.setRawEndTime(attendance.getClockOut());
                attendanceReport.setRawAttendanceTime(attendanceTime);
                if (overwrite) {
                    attendanceReport.setStartTime(attendance.getClockIn());
                    attendanceReport.setEndTime(attendance.getClockOut());
                    attendanceReport.setAttendanceTime(attendanceTime);
                    attendanceReport.setLeaveTime(0.0);
                    attendanceReport.setStatus(attendanceStatus.ordinal());
                } else {
                    attendanceReport.setStartTime(DateUtil.getMinTime(attendance.getClockIn(), attendanceReport.getStartTime()));
                    attendanceReport.setEndTime(DateUtil.getMaxTime(attendance.getClockOut(), attendanceReport.getEndTime()));
                    attendanceReport.setAttendanceTime(computeAttendanceTime(attendanceReport.getStartTime().toLocalTime(), attendanceReport.getEndTime().toLocalTime()));
                    if (attendanceReport.getStatus() < 1000) {
                        attendanceReport.setLeaveTime(0.0);
                        attendanceReport.setStatus(attendanceStatus.ordinal());
                    }
                }

            }
        }
        attendanceReportRepository.saveAll(attendanceReports);
    }

    private Map<AttendanceMapKey, AttendanceReport> getAttendaceReportMap(List<AttendanceReport> attendanceReports) {
        return attendanceReports
                .stream()
                .collect(Collectors.toMap(
                        report -> AttendanceMapKey.builder()
                                .employeeId(report.getEmployeeId())
                                .date(report.getDate())
                                .build(),
                        report -> report
                ));
    }

    private void setUpTime(AttendanceRange attendanceRange) {
        if (attendanceRange != null &&
                attendanceRange.getStatus().equals(StatusEnum.ACTIVE.ordinal())) {
            startTime = attendanceRange.getStartTime();
            endTime = attendanceRange.getEndTime();
            startLunch = attendanceRange.getStartLunch();
            endLunch = attendanceRange.getEndLunch();
            breakTimeInMinutes = (int) ChronoUnit.MINUTES.between(startLunch, endLunch);
            fullHours = attendanceRange.getFullHours();
        } else {
            startTime = START_TIME;
            endTime = END_TIME;
            startLunch = START_LUNCH;
            endLunch = END_LUNCH;
            breakTimeInMinutes = BREAK_TIME_IN_MINUTES;
            fullHours = 8.0;
        }

    }

    private LocalTime adjustClockIn(LocalTime clockIn, LocalTime startTime) {
        if (clockIn.isBefore(startTime)) {
            return startTime;
        }
        return clockIn;
    }

    private LocalTime adjustClockOut(LocalTime clockOut, LocalTime endTime) {
        if (clockOut.isAfter(endTime)) {
            return endTime;
        }
        return clockOut;
    }

    private boolean isAttendanceCheckTimeInLunchBreak(LocalTime time) {
        return (time.equals(startLunch) || time.isAfter(startLunch)) &&
                (time.equals(endLunch) || time.isBefore(endLunch));
    }


    @Data
    @Builder
    static class AttendanceMapKey {
        private int employeeId;
        private int date;

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AttendanceMapKey that = (AttendanceMapKey) o;
            return employeeId == that.employeeId && date == that.date;
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId, date);
        }
    }
}
