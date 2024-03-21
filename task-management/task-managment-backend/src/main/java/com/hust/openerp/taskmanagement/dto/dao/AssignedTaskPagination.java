package com.hust.openerp.taskmanagement.dto.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Deprecated
public class AssignedTaskPagination {
    private List<TaskDao> data;
    private long totalPage;
}
