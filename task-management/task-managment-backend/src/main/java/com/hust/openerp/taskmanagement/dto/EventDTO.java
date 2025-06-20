package com.hust.openerp.taskmanagement.dto;

import java.util.Date;
import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EventDTO {
  private UUID id;
  private String name;
  private String description;
  private Date startDate;
  private Date dueDate;
  private Date createdStamp;
}
