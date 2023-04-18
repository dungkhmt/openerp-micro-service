package com.hust.baseweb.applications.taskmanagement.dto.dao;

import com.hust.baseweb.entity.StatusItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class StatusTaskDao {
    private StatusItem statusItem;
    private int total;
    private List<TaskDao> taskList;
}
