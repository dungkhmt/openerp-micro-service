package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageInfo;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageableMetaResource extends MetaResource {
    private PageInfo pageInfo;

    public PageableMetaResource(PageInfo pageInfo) {
        super(ResponseCode.OK);
        this.pageInfo = pageInfo;
    }

    public PageableMetaResource(ResponseCode responseCode) {
        super(responseCode);
        this.pageInfo = null;
    }
}


