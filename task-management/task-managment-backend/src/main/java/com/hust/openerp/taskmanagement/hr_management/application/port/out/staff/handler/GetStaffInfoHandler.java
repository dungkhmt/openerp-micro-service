package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.GetStaffInfo;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_department.usecase_data.GetCurrentDepartment;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data.GetCurrentJobPosition;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.InvalidParameterException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
