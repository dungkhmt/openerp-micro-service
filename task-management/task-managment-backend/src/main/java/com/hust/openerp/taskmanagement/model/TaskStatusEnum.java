package com.hust.openerp.taskmanagement.model;

import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskStatusEnum {
    TASK_OPEN("TASK_OPEN"),
    TASK_INPROGRESS("TASK_INPROGRESS"),
    TASK_RESOLVED("TASK_RESOLVED"),
    TASK_CLOSED("TASK_CLOSED"),
    ASSIGNMENT_ACTIVE("ASSIGNMENT_ACTIVE"),
    ASSIGNMENT_INACTIVE("ASSIGNMENT_INACTIVE");

    private final String statusId;
}
