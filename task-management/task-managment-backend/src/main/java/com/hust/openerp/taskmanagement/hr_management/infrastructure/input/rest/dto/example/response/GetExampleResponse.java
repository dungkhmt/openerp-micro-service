package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.example.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ExampleModel;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetExampleResponse {
    public static GetExampleResponse fromModel(ExampleModel model) {
        return GetExampleResponse.builder()
                .build();
    }
}
