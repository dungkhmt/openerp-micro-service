package com.hust.openerp.taskmanagement.hr_management.application.port.out.example.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.ExampleModel;

@Data
@Builder
@Getter
@Setter
public class VoidExampleUseCase implements UseCase {
    public ExampleModel toModel() {
        return ExampleModel.builder()
                .build();
    }
}
