package com.hust.openerp.taskmanagement.dto;

import com.hust.openerp.taskmanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupUserDTO {
    private UUID groupId;
    private User user;
    private Date fromDate;
    private Date thrsDate;
    private int totalTasks;
    private int uncompletedTasks;
    private int completedTasks;
    private int inProgressTasks;
}

