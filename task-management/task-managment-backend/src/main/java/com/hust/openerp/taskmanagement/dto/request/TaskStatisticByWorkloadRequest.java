package com.hust.openerp.taskmanagement.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TaskStatisticByWorkloadRequest {
    @NotNull
    private UUID projectId;
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
}
