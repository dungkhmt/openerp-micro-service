package com.hust.openerp.taskmanagement.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.hust.openerp.taskmanagement.entity.User;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EventOverviewDTO {
  private UUID id;
  private String name;
  private String description;
  private Date startDate;
  private Date dueDate;
  private Date createdStamp;
  private List<User> eventUsers;
  private int totalTasks;
  private int finishedTasks;
}
