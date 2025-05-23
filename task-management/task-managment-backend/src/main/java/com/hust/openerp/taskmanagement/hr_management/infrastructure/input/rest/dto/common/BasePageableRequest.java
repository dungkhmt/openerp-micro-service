package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.SortDirection;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class BasePageableRequest implements IPageableRequest {
    private Long page;
    private Long pageSize;
}
