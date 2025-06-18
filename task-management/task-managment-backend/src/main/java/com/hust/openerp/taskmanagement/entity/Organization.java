package com.hust.openerp.taskmanagement.entity;

import com.hust.openerp.taskmanagement.multitenancy.entity.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "task_management_organization")
public class Organization extends AbstractBaseEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "code", length = 256)
    private String code;

    @Column(name = "name", length = 500)
    private String name;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "created_by_user_id", length = 60)
    private String createdBy;
}
