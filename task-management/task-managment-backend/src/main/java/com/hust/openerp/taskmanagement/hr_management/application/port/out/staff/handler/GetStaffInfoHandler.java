package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffPort;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.GetAllStaffInfo;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.GetStaffInfo;
import openerp.openerpresourceserver.application.port.out.staff_department.usecase_data.GetAllCurrentStaffDepartment;
import openerp.openerpresourceserver.application.port.out.staff_department.usecase_data.GetCurrentDepartment;
import openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data.GetAllCurrentStaffJobPosition;
import openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data.GetCurrentJobPosition;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.PageWrapperUseCaseHandler;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;
import openerp.openerpresourceserver.domain.exception.ApplicationException;
import openerp.openerpresourceserver.domain.exception.InvalidParameterException;
import openerp.openerpresourceserver.domain.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetStaffInfoHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<StaffDetailModel, GetStaffInfo>
{
    private final IStaffPort staffPort;

    @Override
    public void init() {
        register(GetStaffInfo.class, this);
    }

    @Override
    public StaffDetailModel handle(GetStaffInfo useCase) {
        StaffModel staffModel;
        if(useCase.getStaffCode() != null){
            staffModel = staffPort.findByStaffCode(useCase.getStaffCode());
        }
        else if(useCase.getUserLoginId() != null){
            staffModel = staffPort.findByUserLoginId(useCase.getUserLoginId());
        }
        else {
            log.error("Staff code or User Login Id is required");
            throw new InvalidParameterException("Staff code or User Login Id is required");
        }
        var userLoginId = staffModel.getUserLoginId();
        return StaffDetailModel.of(staffModel, getDepartment(userLoginId), getJobPosition(userLoginId));
    }

    private DepartmentModel getDepartment(String userLoginId) {
        var getCurrentDepartment = new GetCurrentDepartment(userLoginId);
        return publish(StaffDepartmentModel.class, getCurrentDepartment).getDepartmentModel();
    }

    private JobPositionModel getJobPosition(String userLoginId) {
        var getCurrentJobPosition = new GetCurrentJobPosition(userLoginId);
        return publish(StaffJobPositionModel.class, getCurrentJobPosition).getJobPosition();
    }

}
