package com.hust.openerp.taskmanagement.dto.form;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskForm {
    @NotEmpty
    private String name;
    @Nullable
    private String description;
    @NotNull
    private UUID projectId;
    @Nullable
    private UUID eventId;
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
    private UUID parentId;
    @Nullable
    private List<String> skillIdList;
}
