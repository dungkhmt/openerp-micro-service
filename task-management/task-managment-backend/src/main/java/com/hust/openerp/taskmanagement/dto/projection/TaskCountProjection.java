package com.hust.openerp.taskmanagement.dto.projection;

public interface TaskCountProjection {
    String getUserId();

    String getStatusId();

    long getCount();
}