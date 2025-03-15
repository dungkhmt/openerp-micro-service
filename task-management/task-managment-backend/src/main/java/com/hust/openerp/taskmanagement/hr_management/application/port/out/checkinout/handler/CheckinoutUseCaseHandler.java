package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckinoutPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.usecase_data.Checkinout;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckinoutModel;

import java.time.LocalDateTime;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class CheckinoutUseCaseHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<Checkinout>{
    private final ICheckinoutPort checkinoutPort;

    @Override
    public void init() {
        register(Checkinout.class, this);
    }

    @Override
    public void handle(Checkinout useCase) {
        checkinoutPort.checkinout(useCase);
    }
}
