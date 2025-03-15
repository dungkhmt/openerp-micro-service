package com.hust.openerp.taskmanagement.hr_management.application.port.out.example.usecase_data;

import lombok.*;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ExampleModel;

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
