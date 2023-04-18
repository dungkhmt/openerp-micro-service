package com.hust.baseweb.applications.taskmanagement.dto.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HistoryDao {
    private String date;
    private List<TaskExecutionDao> taskExecutionList;
}
