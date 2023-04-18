package com.hust.baseweb.applications.taskmanagement.dto.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignedTaskPagination {
    private List<TaskDao> data;
    private long totalPage;
}
