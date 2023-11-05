package com.hust.openerp.taskmanagement.dto.form;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class TaskStatusForm {

    private String statusId;
    private String assignee;
    private Date dueDate;
}
