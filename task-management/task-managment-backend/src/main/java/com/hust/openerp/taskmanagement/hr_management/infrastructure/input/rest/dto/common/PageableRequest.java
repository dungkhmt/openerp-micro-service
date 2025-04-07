package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageableRequest implements IPageableRequest {
    private Long page;
    private Long pageSize;
    private String sortBy;
    private String order;
}
