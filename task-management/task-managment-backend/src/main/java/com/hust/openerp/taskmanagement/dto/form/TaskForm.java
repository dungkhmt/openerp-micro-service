package com.hust.openerp.taskmanagement.dto.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskForm {
    private String name;
    private String description;
    private Date fromDate;
    private Date dueDate;
    private String attachmentPaths;
    private UUID projectId;
    private String statusId;
    private String priorityId;
    private String categoryId;
    private String assigneeId;
    @Min(0)
    private Integer estimatedTime;
    @Min(0)
    @Max(100)
    private Integer progress;
    private String note;
    private UUID parentId;
    // private List<String> skillIds;
}
