package com.hust.openerp.taskmanagement.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "entity_authorization")
public class EntityAuthorization {
  @Id
  private String id;
  private String roleId;
  @Column(name = "last_updated")
  private Instant lastUpdated;

  @Column(name = "created")
  private Instant created;

  @Lob
  @Column(name = "description")
  private String description;

}
