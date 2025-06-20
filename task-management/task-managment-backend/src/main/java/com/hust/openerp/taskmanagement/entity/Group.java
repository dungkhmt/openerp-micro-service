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
@Table(name = "task_management_group")
public class Group extends AbstractBaseEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_by_user_id", length = 60)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_stamp")
    private Date createdStamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", insertable = false, updatable = false)
    private User creator;
}
