package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.constant.SortDirection;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageableRequest extends BasePageableRequest {
    private String sortBy = "id";
    private SortDirection order = SortDirection.DESC;
}
