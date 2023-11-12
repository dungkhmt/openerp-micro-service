package com.hust.openerp.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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
