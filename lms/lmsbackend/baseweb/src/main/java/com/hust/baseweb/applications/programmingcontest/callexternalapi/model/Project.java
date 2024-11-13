package com.hust.baseweb.applications.programmingcontest.callexternalapi.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
  private UUID id;

  private String name;

  private String code;

  private String description;

  private String createdStamp;

  private String lastUpdatedStamp;
}
