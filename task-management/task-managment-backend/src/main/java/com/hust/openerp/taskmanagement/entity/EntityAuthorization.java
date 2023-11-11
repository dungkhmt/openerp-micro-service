package com.hust.openerp.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

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
