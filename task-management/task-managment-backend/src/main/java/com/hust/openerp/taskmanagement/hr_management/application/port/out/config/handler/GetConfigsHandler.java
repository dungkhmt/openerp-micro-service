package com.hust.openerp.taskmanagement.hr_management.application.port.out.config.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IConfigPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.config.usecase_data.GetConfigs;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ConfigModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetConfigsHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<Map<ConfigKey, ConfigModel>, GetConfigs>
{
    private final IConfigPort configPort;

    @Override
    public void init() {
        register(GetConfigs.class,this);
    }

    @Override
    public Map<ConfigKey, ConfigModel> handle(GetConfigs useCase) {
        return configPort.getConfigsInGroup(useCase.getConfigGroup());
    }
}
