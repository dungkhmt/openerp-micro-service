package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageInfo;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageableResponse<T> {
    List<T> pageContent;
    PageInfo pageInfo;
    public static <T> PageableResponse<T> of(PageWrapper<T> wrapper) {
        var pageableResponse = new PageableResponse<T>();
        pageableResponse.pageContent = wrapper.getPageContent();
        pageableResponse.pageInfo = wrapper.getPageInfo();
        return pageableResponse;
    }
}
