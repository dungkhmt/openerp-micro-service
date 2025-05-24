package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckinoutPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.GetCheckinout;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckinoutModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCheckinoutHandler extends ObservableUseCasePublisher implements CollectionUseCaseHandler<CheckinoutModel, GetCheckinout> {
    private final ICheckinoutPort checkinoutPort;

    @Override
    public void init() {
        register(GetCheckinout.class, this);
    }

    @Override
    public Collection<CheckinoutModel> handle(GetCheckinout useCase) {
        return checkinoutPort.getCheckinout(useCase);
    }
}
