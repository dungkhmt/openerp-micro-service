package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource;

import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.model.PageInfo;
import openerp.openerpresourceserver.domain.model.PageWrapper;

@Getter
@Setter
public class Resource {
    private Object data;
    private Object meta;

    public Resource(Object data) {
        this.meta = new MetaResource(ResponseCode.OK);
        this.data = data;
    }

    public Resource(PageWrapper<?> pageWrapper) {
        this.meta = new PageableMetaResource(pageWrapper.getPageInfo());
        this.data = pageWrapper.getPageContent();
    }

    public Resource() {
        this.meta = new MetaResource(ResponseCode.OK);
        this.data = null;
    }

    public Resource(Long code, String message){
        this.meta = new MetaResource(code, message);
        this.data = null;
    }

    public Resource(Long code, String message, Object data){
        this.meta = new MetaResource(code, message);
        this.data = data;
    }

    public Resource(ResponseCode responseCode, Object data){
        this.meta = new MetaResource(responseCode);
        this.data = data;
    }
}

