package com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.service;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.GetAbsenceList;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.GetAttendances;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.holiday.usecase_data.GetHolidayList;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.payroll.usecase_data.CreatePayroll;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.GetAllStaffInfo;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.GetAllCurrentStaffSalary;
import com.hust.openerp.taskmanagement.hr_management.constant.*;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.*;
import com.hust.openerp.taskmanagement.util.WorkTimeCalculator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class PayrollCalculator extends BeanAwareUseCasePublisher {
    private final CreatePayroll createPayroll;
    private final CompanyConfigModel companyConfig;
    private final Set<LocalDate> holidayDates;

    @Getter
    private final PayrollModel payrollModel;
    @Getter
    private final List<PayrollDetailModel> payrollDetailModels;

    public PayrollCalculator(
        CompanyConfigModel companyConfig,
        CreatePayroll createPayroll
    ) {
        this.createPayroll = createPayroll;
        this.companyConfig = companyConfig;
        holidayDates = getHolidays(createPayroll.getFromDate(), createPayroll.getThruDate());
        payrollModel = calculatePayrollModel();
        payrollDetailModels = calculatePayrollDetailsModel();
    }

    private PayrollModel calculatePayrollModel() {
        return PayrollModel.builder()
            .name(createPayroll.getName())
            .totalWorkDays(getTotalWorkDays(createPayroll.getFromDate(), createPayroll.getThruDate()))
            .workHoursPerDay((float) roundToQuarter(companyConfig.getTotalWorkTime()))
            .totalHolidayDays(holidayDates.size())
            .fromDate(createPayroll.getFromDate())
            .thruDate(createPayroll.getThruDate())
            .createdBy(createPayroll.getCreatedBy())
            .status(PayrollStatus.ACTIVE)
            .build();
    }

    private List<PayrollDetailModel> calculatePayrollDetailsModel() {
        var staffs = getAllStaffs();
        var userIdOfStaffs = staffs.stream().map(StaffDetailModel::getUserLoginId).toList();
        var salaryMap = getStaffSalaries(userIdOfStaffs);
        var absenceMap = getStaffAbsences(userIdOfStaffs, createPayroll.getFromDate(), createPayroll.getThruDate());
        var attendanceMap = getStaffAttendances(userIdOfStaffs, createPayroll.getFromDate(), createPayroll.getThruDate());

        return staffs.stream().map(staff -> {
            var userId = staff.getUserLoginId();
            var salary = salaryMap.get(userId);
            var attendance = attendanceMap.get(userId);
            var absences = absenceMap.getOrDefault(userId, List.of()).stream()
                .collect(Collectors.groupingBy(AbsenceModel::getDate));

            float totalWorkHours = 0;
            float totalPairLeaveHours = 0;
            float totalUnpairLeaveHours = 0;

            List<Double> workHoursInDays = new ArrayList<>();
            List<Double> absenceHoursInDays = new ArrayList<>();

            LocalDate currentDate = createPayroll.getFromDate();
            var workTimePerDay = roundToQuarter(companyConfig.getTotalWorkTime());
            while (!currentDate.isAfter(createPayroll.getThruDate())) {
                boolean isWeekend = currentDate.getDayOfWeek().getValue() == 6 || currentDate.getDayOfWeek().getValue() == 7;
                boolean isHoliday = holidayDates.contains(currentDate);

                double dailyWorkHours = 0;
                double dailyAbsenceHours = 0;

                if (!isWeekend && !isHoliday) {
                    if (attendance != null && attendance.getAttendances().containsKey(currentDate)) {
                        var dayAttendance = attendance.getAttendances().get(currentDate);
                        LocalTime startTime = dayAttendance.getStartTime() != null ? dayAttendance.getStartTime().toLocalTime() : null;
                        LocalTime endTime = dayAttendance.getEndTime() != null ? dayAttendance.getEndTime().toLocalTime() : null;
                        dailyWorkHours = WorkTimeCalculator.calculateWorkTimeByHours(
                            startTime,
                            endTime,
                            companyConfig
                        );
                    }

                    if (absences.containsKey(currentDate)) {
                        var absenceInDay = absences.get(currentDate);
                        for (var absence : absenceInDay) {
                            var absenceHours = roundToQuarter(absence.getDurationTimeAbsence(companyConfig));
                            dailyAbsenceHours += absenceHours;
                            if (absence.getType() == AbsenceType.PAID_LEAVE) {
                                totalPairLeaveHours += (float) absenceHours;
                            } else {
                                totalUnpairLeaveHours += (float) absenceHours;
                            }
                        }
                    }
                } else if (isHoliday && !isWeekend) {
                    dailyAbsenceHours = workTimePerDay;
                    if (salary.getSalaryType() != SalaryType.HOURLY) {
                        totalPairLeaveHours += (float) dailyAbsenceHours;
                    }
                    else {
                        totalUnpairLeaveHours += Math.max(0f, (float) dailyAbsenceHours - (float) dailyWorkHours);
                    }
                }
                if(dailyWorkHours + dailyAbsenceHours > workTimePerDay) {
                    dailyWorkHours = Math.max(0f,  workTimePerDay - dailyAbsenceHours);
                }
                workHoursInDays.add(roundToQuarter(dailyWorkHours));
                absenceHoursInDays.add(roundToQuarter(dailyAbsenceHours));
                totalWorkHours += (float) dailyWorkHours;
                currentDate = currentDate.plusDays(1);
            }
            totalWorkHours = (float) roundToQuarter(totalWorkHours);
            totalPairLeaveHours = (float) roundToQuarter(totalPairLeaveHours);
            totalUnpairLeaveHours = (float) roundToQuarter(totalUnpairLeaveHours);
            return PayrollDetailModel.builder()
                .userId(userId)
                .salary(salary != null ? salary.getSalary() : 0)
                .salaryType(salary != null ? salary.getSalaryType() : null)
                .workHours(workHoursInDays)
                .totalWorkHours(totalWorkHours)
                .absenceHours(absenceHoursInDays)
                .pairLeaveHours(totalPairLeaveHours)
                .unpairLeaveHours(totalUnpairLeaveHours)
                .payrollAmount(calculatePayrollAmount(totalWorkHours, totalPairLeaveHours, salary))
                .build();
        }).toList();
    }

    private Integer calculatePayrollAmount(float workHours, float pairLeaveHours, StaffSalaryModel salary) {
        if (salary == null) {
            return 0;
        }

        float totalHours = workHours + pairLeaveHours;
        switch (salary.getSalaryType()) {
            case MONTHLY -> {
                int totalWorkDays = getTotalWorkDays(createPayroll.getFromDate(), createPayroll.getThruDate());
                float totalWorkHours = totalWorkDays * companyConfig.getTotalWorkTime();
                return Math.round((totalHours / totalWorkHours) * salary.getSalary());
            }
            case WEEKLY -> {
                int totalWeeks = (int) Math.ceil((float) getTotalWorkDays(createPayroll.getFromDate(), createPayroll.getThruDate()) / 7);
                float totalWorkHours = totalWeeks * companyConfig.getTotalWorkTime() * 5; // Assuming 5 workdays per week
                return Math.round((totalHours / totalWorkHours) * salary.getSalary());
            }
            case HOURLY -> {
                return Math.round(totalHours * salary.getSalary());
            }
            default -> {
                return 0;
            }
        }
    }

    private static double roundToQuarter(double value) {
        return Math.round(value * 4) / 4.0;
    }

    private Integer getTotalWorkDays(LocalDate startDate, LocalDate endDate) {
        int totalWorkDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            boolean isWeekend = currentDate.getDayOfWeek().getValue() == 6
                || currentDate.getDayOfWeek().getValue() == 7;

            if (!isWeekend) {
                totalWorkDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return totalWorkDays;
    }

    private List<StaffDetailModel> getAllStaffs() {
        var useCase = new GetAllStaffInfo();
        var staffPage = publishPageWrapper(StaffDetailModel.class, useCase);
        return staffPage.getPageContent();
    }

    private Map<String, AttendanceModel> getStaffAttendances(
        List<String> userIds,
        LocalDate startDate,
        LocalDate endDate) {
        var useCase = GetAttendances.builder()
            .userIds(userIds)
            .startDate(startDate)
            .endDate(endDate)
            .build();
        var attendances = publishCollection(AttendanceModel.class, useCase);
        return attendances.stream()
            .collect(Collectors.toMap(
                AttendanceModel::getUserId,
                s -> s,
                (oldValue, newValue) -> newValue));
    }

    private Map<String, List<AbsenceModel>> getStaffAbsences(
        List<String> userIds,
        LocalDate startDate,
        LocalDate endDate) {
        var useCase = GetAbsenceList.builder()
            .userIds(userIds)
            .startDate(startDate)
            .endDate(endDate)
            .status(AbsenceStatus.ACTIVE)
            .build();
        var absenceList = publishCollection(AbsenceModel.class, useCase);
        return absenceList.stream()
            .collect(Collectors.groupingBy(AbsenceModel::getUserId));
    }

    private Set<LocalDate> getHolidays(
        LocalDate startDate,
        LocalDate endDate) {
        var useCase = new GetHolidayList(startDate, endDate);
        var model = publish(HolidayListModel.class, useCase);
        return model.getHolidays().values().stream()
            .flatMap(holiday -> holiday.getDates().stream())
            .collect(Collectors.toSet());
    }

    private Map<String, StaffSalaryModel> getStaffSalaries(List<String> userIds) {
        var useCase = new GetAllCurrentStaffSalary(userIds);
        var staffSalaries = publishCollection(StaffSalaryModel.class, useCase);
        return staffSalaries.stream()
            .collect(Collectors.toMap(
                StaffSalaryModel::getUserLoginId,
                s -> s,
                (oldValue, newValue) -> newValue));
    }
}
