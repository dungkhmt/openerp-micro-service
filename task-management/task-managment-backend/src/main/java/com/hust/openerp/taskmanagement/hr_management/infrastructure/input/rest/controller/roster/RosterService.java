package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.GetAbsenceList;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data.GetShiftList;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.FindStaff;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel; // Giả sử đây là model cho ca đã có
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RosterService extends BeanAwareUseCasePublisher {

    private final OrToolsSolverService solverService;
    private boolean feasibleSolutionFound;


    @Autowired
    public RosterService(OrToolsSolverService solverService) {
        this.solverService = solverService;
    }

    public boolean wasFeasible() {
        return feasibleSolutionFound;
    }

    private List<StaffModel> fetchAllEmployees(List<String> departmentCodes, List<String> jobPositionCodes) {
        var staffPage = publishPageWrapper(
            StaffModel.class,
            FindStaff.builder()
                .departmentCodes(departmentCodes)
                .jobPositionCodes(jobPositionCodes)
                .status(StaffStatus.ACTIVE) // Chỉ lấy nhân viên đang hoạt động
                .build()
        );
        return staffPage.getPageContent();
    }

    private List<AbsenceModel> fetchLeaveRequestsForUsers(List<String> userLoginIds, LocalDate apiStartDate, LocalDate apiEndDate) {
        var useCase = GetAbsenceList.builder()
            .userIds(userLoginIds)
            .startDate(apiStartDate)
            .endDate(apiEndDate)
            .status(AbsenceStatus.ACTIVE)
            .build();
        return publishCollection(AbsenceModel.class, useCase).stream().toList();
    }

    private List<ShiftModel> fetchExistingShiftsForUsers(List<String> userLoginIds, LocalDate apiStartDate, LocalDate apiEndDate) { //
        var useCase = GetShiftList.builder()
            .userIds(userLoginIds)
            .startDate(apiStartDate)
            .endDate(apiEndDate)
            .build();
        return publishCollection(ShiftModel.class, useCase).stream().toList();
    }


    public List<ScheduledShift> generateSchedule(RosterRequest requestDto) {
        this.feasibleSolutionFound = false;

        var allActiveEmployees = fetchAllEmployees(requestDto.getDepartmentCodes(), requestDto.getJobPositionCodes());

        if (allActiveEmployees.isEmpty()) {
            System.out.println("Không có nhân viên nào thỏa mãn tiêu chí phòng ban/chức vụ.");
            this.feasibleSolutionFound = true; // Không có NV, coi như khả thi vì không có gì để lỗi
            return new ArrayList<>();
        }
        System.out.println("Filtered employees count: " + allActiveEmployees.size());


        List<String> employeeUserLoginIds = allActiveEmployees.stream()
            .map(StaffModel::getUserLoginId)
            .distinct()
            .collect(Collectors.toList());

        var approvedLeaves = fetchLeaveRequestsForUsers(employeeUserLoginIds, requestDto.getStartDate(), requestDto.getEndDate());
        System.out.println("Fetched approved leaves count: " + approvedLeaves.size());

        var existingAssignedShifts = fetchExistingShiftsForUsers(employeeUserLoginIds, requestDto.getStartDate(), requestDto.getEndDate()); //
        System.out.println("Fetched existing assigned shifts count: " + existingAssignedShifts.size());

        List<ScheduledShift> schedule = solverService.solveRoster(requestDto, allActiveEmployees, approvedLeaves, existingAssignedShifts);
        this.feasibleSolutionFound = solverService.wasLastSolveFeasible();

        return schedule;
    }
}
