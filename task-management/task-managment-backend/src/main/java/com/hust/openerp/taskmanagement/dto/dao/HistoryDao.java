package com.hust.openerp.taskmanagement.dto.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Deprecated
public class HistoryDao {
    private String date;
    private List<TaskExecutionDao> taskExecutionList;
}
