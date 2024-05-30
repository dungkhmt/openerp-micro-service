package com.hust.openerp.taskmanagement.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TaskStatisticByStatusDTO {
    private String status;
    private Long count;
    private Integer percentage;
    private List<TaskDTO> tasks;

    public TaskStatisticByStatusDTO(String status) {
        this.status = status;
    }

    public TaskStatisticByStatusDTO(String status, Long count) {
        this.status = status;
        this.count = count;
    }
}
