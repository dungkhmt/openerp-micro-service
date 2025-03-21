package com.hust.openerp.taskmanagement.dto.form;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskForm {
    @Nullable
    private String name;
    @Nullable
    private String description;
    @Nullable
    private Date fromDate;
    @Nullable
    private Date dueDate;
    @Nullable
    private String attachmentPaths;
    @Nullable
    private String statusId;
    @Nullable
    private String priorityId;
    @Nullable
    private String categoryId;
    @Nullable
    private String assigneeId;
    @Min(0)
    private Integer estimatedTime;
    @Min(0)
    @Max(100)
    @Nullable
    private Integer progress;
    @Nullable
    private String note;
    @Nullable
    private List<String> skillIdList;
    @Nullable
    private UUID eventId;
}
