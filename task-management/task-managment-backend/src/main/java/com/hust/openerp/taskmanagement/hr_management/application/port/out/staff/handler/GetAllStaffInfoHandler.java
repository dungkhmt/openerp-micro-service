package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.GetAllStaffInfo;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data.GetAllCurrentStaffDepartment;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data.GetAllCurrentStaffJobPosition;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.PageWrapperUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllStaffInfoHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<StaffDetailModel, GetAllStaffInfo>
{
    private final IStaffPort staffPort;

    @Override
    public void init() {
        register(GetAllStaffInfo.class, this);
    }

    @Override
    public PageWrapper<StaffDetailModel> handle(GetAllStaffInfo useCase) {
        var staffPage = staffPort.findStaff(useCase, useCase.getPageableRequest());
        var userLoginIds = staffPage.getPageContent().stream()
                .map(StaffModel::getUserLoginId)
                .toList();
        var departmentMap = getDepartmentMap(userLoginIds);
        var jobPositionMap = getJobPositionMap(userLoginIds);
        return staffPage.convert(staffModel -> {
            var department = departmentMap.getOrDefault(staffModel.getUserLoginId(), null);
            var jobPosition = jobPositionMap.getOrDefault(staffModel.getUserLoginId(), null);
            return StaffDetailModel.of(staffModel, department, jobPosition);
        });
    }

    private Map<String, DepartmentModel> getDepartmentMap(List<String> userLoginIds) {
        var getAllCurrentDepartment = new GetAllCurrentStaffDepartment(userLoginIds);
        var staffDepartments = publishCollection(StaffDepartmentModel.class, getAllCurrentDepartment);
        return staffDepartments.stream()
                .collect(Collectors.toMap(
                        StaffDepartmentModel::getUserLoginId,
                        StaffDepartmentModel::getDepartmentModel
                ));
    }

    private Map<String, JobPositionModel> getJobPositionMap(List<String> userLoginIds) {
        var getAllCurrentJobPosition = new GetAllCurrentStaffJobPosition(userLoginIds);
        var staffJobPositions = publishCollection(StaffJobPositionModel.class, getAllCurrentJobPosition);
        return staffJobPositions.stream()
                .collect(Collectors.toMap(
                        StaffJobPositionModel::getUserLoginId,
                        StaffJobPositionModel::getJobPosition
                ));
    }

}
