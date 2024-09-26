package com.hust.openerp.taskmanagement.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TaskStatisticByStatusRequest {
    @NotBlank
    private String status;
    @NotNull
    private UUID projectId;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
    private boolean includeTasks = false;
}
