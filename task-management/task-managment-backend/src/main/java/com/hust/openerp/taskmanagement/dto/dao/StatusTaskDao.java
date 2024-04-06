package com.hust.openerp.taskmanagement.dto.dao;

import com.hust.openerp.taskmanagement.entity.StatusItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Deprecated
public class StatusTaskDao {
    private StatusItem statusItem;
    private int total;
    private List<TaskDao> taskList;
}
