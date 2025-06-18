package com.hust.openerp.taskmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultTenantId {
    DEFAULT_TENANT_ID("shared");

    private final String id;
}
