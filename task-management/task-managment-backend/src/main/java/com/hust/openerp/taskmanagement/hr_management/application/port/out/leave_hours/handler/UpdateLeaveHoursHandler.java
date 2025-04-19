package com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IJobPositionPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data.GetJobPosition;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.usecase_data.UpdateLeaveHours;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.FindStaff;
import com.hust.openerp.taskmanagement.hr_management.constant.JobPositionType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateLeaveHoursHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateLeaveHours>
{
    private final IStaffPort staffPort;
    private final IJobPositionPort jobPositionPort;

    @Override
    public void init() {
        register(UpdateLeaveHours.class,this);
    }

    @Override
    public void handle(UpdateLeaveHours useCase) {
        if(useCase.getStaffFilter() == null){
            //set default update: all fullTime staffs
            var fullTimeJobFilter = GetJobPosition.builder().type(JobPositionType.FULL_TIME).build();
            var fullTimeJobCodes = jobPositionPort.getJobPosition(fullTimeJobFilter).stream()
                .map(JobPositionModel::getCode)
                .toList();
            useCase.setStaffFilter(FindStaff.builder().jobPositionCodes(fullTimeJobCodes).build());
        }

        staffPort.updateLeaveHours(useCase.getStaffFilter(), useCase.getLeaveHours(), useCase.getUpdateType());
    }
}
