package com.hust.openerp.taskmanagement.hr_management.application.port.out.example.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ExampleModel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
