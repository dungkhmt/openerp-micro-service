package com.hust.openerp.taskmanagement.dto.form;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class BoardFilterInputForm {
    private UUID projectId;
    private String categoryId;
    private String userId;
    private String priorityId;
    private Date startDate;
    private Date endDate;
    private String keyName;
}
