package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller;


import com.hust.openerp.taskmanagement.hr_management.application.port.out.config.usecase_data.GetConfigs;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigGroup;
import com.hust.openerp.taskmanagement.hr_management.constant.ConfigKey;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.BeanAwareUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ConfigModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.Resource;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.config.request.UpdateConfigsRequest;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.config.response.ConfigResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/configs")
public class ConfigController extends BeanAwareUseCasePublisher {

    @GetMapping
    public ResponseEntity<?> getConfigs(
        @Valid @RequestParam ConfigGroup configGroup
    ){
        var useCase = new GetConfigs(configGroup);
        @SuppressWarnings("unchecked")
        var modelMap = (Map<ConfigKey, ConfigModel>) publish(Map.class ,useCase);
        var responseMap = modelMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> ConfigResponse.fromModel(entry.getValue())
            ));
        return ResponseEntity.ok().body(
            new Resource(responseMap)
        );
    }

    @PutMapping
    public ResponseEntity<?> updateConfigs(
        @Valid @RequestBody UpdateConfigsRequest request
    ){
        publish(request.toUseCase());
        return ResponseEntity.ok().body(
            new Resource()
        );
    }
}
