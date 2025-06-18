package com.hust.openerp.taskmanagement.dto.projection;

import java.util.UUID;

public interface OrganizationOverviewProjection {
    UUID getId();

    String getName();

    String getCode();

    long getMemberCount();

    String getMyRole();
}
