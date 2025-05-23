package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.GetAbsenceList;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.FindStaff;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.StaffStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
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
    private final ObjectMapper objectMapper; // Để parse JSON
    private boolean feasibleSolutionFound;


    @Autowired
    public RosterService(OrToolsSolverService solverService, ObjectMapper objectMapper) {
        this.solverService = solverService;
        this.objectMapper = objectMapper;
    }

    public boolean wasFeasible() {
        return feasibleSolutionFound;
    }

    // Wrapper class cho API response (giống cấu trúc bạn cung cấp)
    static class ApiResponse<T> {
        public List<T> data;
        public Meta meta;
        static class Meta { public String code; public String message;}
    }


    private List<StaffModel> fetchAllEmployees() {
        //todo filter
        var staffPage = publishPageWrapper(StaffModel.class, FindStaff.builder().status(StaffStatus.ACTIVE).build());
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


    public List<ScheduledShift> generateSchedule(RosterRequest requestDto) {
        this.feasibleSolutionFound = false; // Reset cờ

        // 1. Lấy tất cả nhân viên ACTIVE
        var allActiveEmployees = fetchAllEmployees();

        /*// 2. Lọc nhân viên theo phòng ban và chức vụ từ requestDto (nếu có)
        List<EmployeeDto> filteredEmployees = allActiveEmployees.stream()
            .filter(emp -> (requestDto.getDepartmentCodes() == null || requestDto.getDepartmentCodes().isEmpty() ||
                (emp.getDepartment() != null && requestDto.getDepartmentCodes().contains(emp.getDepartment().getDepartmentCode()))))
            .filter(emp -> (requestDto.getJobPositionCodes() == null || requestDto.getJobPositionCodes().isEmpty() ||
                (emp.getJobPosition() != null && requestDto.getJobPositionCodes().contains(emp.getJobPosition().getJobPositionCode()))))
            .collect(Collectors.toList());*/

        if (allActiveEmployees.isEmpty()) {
            System.out.println("Không có nhân viên nào thỏa mãn tiêu chí phòng ban/chức vụ.");
            return new ArrayList<>();
        }
        System.out.println("Filtered employees count: " + allActiveEmployees.size());


        // 3. Lấy ngày nghỉ phép của các nhân viên đã lọc
        List<String> employeeUserLoginIds = allActiveEmployees.stream()
            .map(StaffModel::getUserLoginId)
            .distinct()
            .collect(Collectors.toList());
        var approvedLeaves = fetchLeaveRequestsForUsers(employeeUserLoginIds, requestDto.getStartDate(), requestDto.getEndDate());
        System.out.println("Fetched approved leaves count: " + approvedLeaves.size());

        // 4. Gọi solver
        List<ScheduledShift> schedule = solverService.solveRoster(requestDto, allActiveEmployees, approvedLeaves);
        this.feasibleSolutionFound = solverService.wasLastSolveFeasible();

        return schedule;
    }
}