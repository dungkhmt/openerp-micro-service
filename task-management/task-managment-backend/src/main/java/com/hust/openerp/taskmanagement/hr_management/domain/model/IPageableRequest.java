package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.SortDirection;

public interface IPageableRequest {
    Long getPage();
    Long getPageSize();
    String getSortBy();
    SortDirection getOrder();
}
