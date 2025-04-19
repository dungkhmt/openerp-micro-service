package com.hust.openerp.taskmanagement.hr_management.application.port.out.config.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IConfigPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.config.usecase_data.UpdateConfigs;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateConfigsHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateConfigs>
{
    private final IConfigPort configPort;

    @Override
    public void init() {
        register(UpdateConfigs.class,this);
    }

    @Override
    public void handle(UpdateConfigs useCase) {
        configPort.updateConfigs(useCase.getConfigValueMap());
    }
}
