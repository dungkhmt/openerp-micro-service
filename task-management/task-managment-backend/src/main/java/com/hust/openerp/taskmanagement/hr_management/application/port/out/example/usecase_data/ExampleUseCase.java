package com.hust.openerp.taskmanagement.hr_management.application.port.out.example.usecase_data;

import lombok.*;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.ExampleModel;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
public class ExampleUseCase implements UseCase {
    public ExampleModel toModel() {
        return ExampleModel.builder()
                .build();
    }
}
