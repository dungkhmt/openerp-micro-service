package com.hust.baseweb.applications.taskmanagement.dto.form;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class TaskStatusForm {

    private String statusId;
    private UUID partyId;
    private Date dueDate;
}
