package com.hust.openerp.taskmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusType {

  @Id
  @Column(name = "status_type_id")
  private String id;

  @JoinColumn(name = "parent_type_id", referencedColumnName = "status_type_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private StatusType parentType;

  private String description;

  private Date createdStamp;

  private Date lastUpdatedStamp;
}
