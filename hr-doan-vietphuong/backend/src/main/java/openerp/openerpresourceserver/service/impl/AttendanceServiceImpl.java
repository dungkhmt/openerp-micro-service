package openerp.openerpresourceserver.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.dto.request.attendance.SyncAttendanceRequest;
import openerp.openerpresourceserver.dto.response.attendance.AttendanceReportResponse;
import openerp.openerpresourceserver.dto.response.attendance.AttendanceSummaryResponse;
import openerp.openerpresourceserver.entity.*;
import openerp.openerpresourceserver.enums.AbsenceTypeEnum;
import openerp.openerpresourceserver.enums.AttendanceStatusEnum;
import openerp.openerpresourceserver.enums.HolidayTypeEnum;
import openerp.openerpresourceserver.enums.StatusEnum;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.service.AttendanceRangeService;
import openerp.openerpresourceserver.service.AttendanceService;
import openerp.openerpresourceserver.service.HolidayService;
import openerp.openerpresourceserver.service.SyncAttendanceService;
import openerp.openerpresourceserver.util.DateUtil;
import openerp.openerpresourceserver.util.ObjectUtil;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static openerp.openerpresourceserver.util.Constants.NO_ABSENCE;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final FaceRecognitionService faceRecognitionService;
    private final SyncAttendanceService syncAttendanceService;
    private final AttendanceRangeService attendanceRangeService;
    private final HolidayService holidayService;
    private final AttendanceRepository attendanceRepository;
    private final AbsenceTypeRepository absenceTypeRepository;
    private final AttendanceReportRepository attendanceReportRepository;
    private final AttendanceRangeRepository attendanceRangeRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Attendance recordAttendance(Mat face, HttpServletRequest request) {
        int employeeId = faceRecognitionService.recognize(face);

        if (employeeId == -1) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        int currentDate = Integer.parseInt(now.toLocalDate().toString().replace("-", ""));

        Attendance attendance = new Attendance();
        attendance.setId(employeeId);
        attendance.setTime(now);
        attendance.setDate(currentDate);
        attendance.setIp(getClientIp(request));
        attendance.setEmployee(employeeRepository.findByEmployeeIdAndStatus(employeeId, StatusEnum.ACTIVE.ordinal()));
        attendance = attendanceRepository.save(attendance);
        syncAttendanceService.syncAttendanceRealTime(
                SyncAttendanceRequest.builder()
                        .id(attendance.getId())
                        .time(attendance.getTime())
                        .date(attendance.getDate())
                        .ip(attendance.getIp())
                        .build()
        );
        return attendance;
    }

    @Override
    public List<AttendanceReportResponse> getAttendanceReport(LocalDate startDate, LocalDate endDate) {
        List<AbsenceType> absenceTypeList = absenceTypeRepository.findByStatus(StatusEnum.ACTIVE.ordinal());
        LocalDate endDateValue = getEndDate(endDate);
        List<Integer> currentCycleHolidays = holidayService.getAttendanceHoliday(startDate, endDate);
        List<AttendanceReport> attReports = attendanceReportRepository
                .findByEmployeeIdAndDateBetween(
                        SecurityUtil.getEmployeeId(),
                        DateUtil.convertLocalDateToInteger(startDate),
                        DateUtil.convertLocalDateToInteger(endDateValue));
        AttendanceRange attendanceRange = attendanceRangeRepository.findByEmployeeEmail(SecurityUtil.getUserEmail());
        double fullHours = attendanceRange.getFullHours();
        List<AttendanceReportResponse> attResponses = attReports.stream().map(attReport -> {
                    if (currentCycleHolidays.contains(attReport.getDate())) {
                        Holiday holiday = holidayService.getHolidayByDate(HolidayTypeEnum.HOLIDAY.ordinal(), attReport.getDate());
                        return AttendanceReportResponse.builder()
                                .date(DateUtil.convertIntegerToLocalDate(attReport.getDate()))
                                .attendanceStatus(AttendanceStatusEnum.HOLIDAY.name())
                                .note(holiday != null ? holiday.getNote() : null)
                                .build();
                    }
                    ObjectUtil.AttendanceReportValue attReportValue = determineAttendanceReportValue(attReport, absenceTypeList, attendanceRange);
                    return AttendanceReportResponse.builder()
                            .startTime(DateUtil.getLocalTime(attReport.getStartTime()))
                            .endTime(DateUtil.getLocalTime(attReport.getEndTime()))
                            .note(attReportValue.note())
                            .bonusTime(attReportValue.bonusTime())
                            .attendanceTime(Math.min(fullHours, attReportValue.attendanceTime() + attReportValue.leaveTime() + attReportValue.bonusTime()))
                            .date(DateUtil.convertIntegerToLocalDate(attReport.getDate()))
                            .attendanceStatus(attReportValue.status())
                            .updatedAt(attReport.getUpdatedAt())
                            .build();
                }
        ).collect(Collectors.toList());

        // populate the no-work-time days, weekends and holidays
        List<Integer> workDays = attReports.stream().map(AttendanceReport::getDate).toList();
        List<Integer> dateRange = DateUtil.getDatesInDateRange(startDate, endDateValue);
        // remove all the day with time, keep the day without time to add
        // "NO RECORD" OR "WEEKEND" or "HOLIDAY" on that day
        dateRange.removeAll(workDays);
        for (Integer date : dateRange) {
            AttendanceReportResponse.AttendanceReportResponseBuilder reportResponse = AttendanceReportResponse.builder()
                    .date(DateUtil.convertIntegerToLocalDate(date));
            if (currentCycleHolidays.contains(date)) {
                Holiday holiday = holidayService.getHolidayByDate(HolidayTypeEnum.HOLIDAY.ordinal(), date);
                attResponses.add(
                        reportResponse
                                .attendanceStatus(AttendanceStatusEnum.HOLIDAY.name())
                                .note(holiday != null ? holiday.getNote() : null)
                                .build());
            } else if (DateUtil.isWeekend(date)) {
                attResponses.add(
                        reportResponse
                                .attendanceStatus(AttendanceStatusEnum.WEEKEND.name())
                                .build());
            } else {
                attResponses.add(
                        reportResponse
                                .attendanceTime(0.0)
                                .attendanceStatus(AttendanceStatusEnum.NO_RECORD.name())
                                .build()
                );
            }
        }
        attResponses.sort(Comparator.comparing(AttendanceReportResponse::date));
        return attResponses;
    }

    @Override
    public AttendanceSummaryResponse getAttendanceSummary(LocalDate start, LocalDate end) {
        Employee employee = employeeRepository.findByEmail(SecurityUtil.getUserEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        List<Integer> requiredWorkDays = DateUtil.getWeekdayInDateRange(start, end);
        List<Integer> holidayList = holidayService.getAttendanceHoliday(start, end);
        LocalDate endDateValue = getEndDate(end);
        List<AttendanceReportResponse> attReports = getAttendanceReport(start, endDateValue);
        double totalWorkHours = attReports.stream()
                .filter(Objects::nonNull)
                .filter(att -> att.attendanceTime() != null)
                .mapToDouble(AttendanceReportResponse::attendanceTime)
                .sum();
        return AttendanceSummaryResponse.builder()
                .totalWorkHours(totalWorkHours)
                .totalWorkDays(Math.round((totalWorkHours / 8.0) * 10) / 10.0)
                .absenceDayLeft(employee.getAnnualLeave())
                .requiredWorkDays(requiredWorkDays.size())
                .totalHolidays(holidayList.size())
                .build();
    }
    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    private LocalDate getEndDate(LocalDate endDate) {
        return endDate.isAfter(LocalDate.now()) ?
                LocalDate.now() :
                endDate;
    }

    private ObjectUtil.AttendanceReportValue determineAttendanceReportValue(
            AttendanceReport attReport,
            List<AbsenceType> absenceTypeList,
            AttendanceRange attendanceRange) {
        String status;
        double leaveTime = attReport.getLeaveTime() == null ? 0.0 : attReport.getLeaveTime();
        double attendanceTime = attReport.getAttendanceTime();
        double fullHours = attendanceRange.getFullHours();
        if (attReport.getStatus() > 1000) {
            AbsenceType absenceType = absenceTypeList
                    .stream()
                    .filter(type -> type.getId().intValue() == attReport.getStatus() - 1000)
                    .findFirst()
                    .orElse(null);
            status = absenceType != null ? AbsenceTypeEnum.values()[absenceType.getType()].name() : null;
            if (absenceType != null &&
                    absenceType.getType() == AbsenceTypeEnum.ABSENCE.ordinal() &&
                    !absenceType.getCode().equals(NO_ABSENCE) &&
                    leaveTime != fullHours) {
                double computedAttendanceTime = attendanceRangeService.getAttendanceTime(
                        attendanceRange,
                        leaveTime,
                        attReport.getStartTime().toLocalTime(),
                        attReport.getEndTime().toLocalTime());
                leaveTime = fullHours / 2;
                attendanceTime = computedAttendanceTime == 4 ? fullHours / 2 : Math.round(computedAttendanceTime * 10.0) / 10.0;
            }
        } else {
            status = AttendanceStatusEnum.values()[attReport.getStatus()].name();
            Holiday holiday = holidayService.getHolidayByDate(HolidayTypeEnum.MCC.ordinal(), attReport.getDate());
            if (holiday != null) {
                status = HolidayTypeEnum.MCC.name();
                return new ObjectUtil.AttendanceReportValue(status, leaveTime, attendanceTime, holiday.getBonusTime(), holiday.getNote());
            }
        }
        return new ObjectUtil.AttendanceReportValue(status, leaveTime, attendanceTime, 0.0, null);
    }
}
