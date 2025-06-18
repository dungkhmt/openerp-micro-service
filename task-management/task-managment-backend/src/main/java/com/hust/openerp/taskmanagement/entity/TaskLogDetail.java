package com.hust.openerp.taskmanagement.entity;

import com.hust.openerp.taskmanagement.multitenancy.entity.AbstractBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "task_management_task_log_detail")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLogDetail extends AbstractBaseEntity {
    @Id
    @SequenceGenerator(name = "task_management_task_log_detail_generator", sequenceName = "task_management_task_log_detail_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "task_management_task_log_detail_generator")
    @Column
    private Long id;

    @Column(name = "event")
    private String event;

    @Column(name = "log_id")
    private Long logId;

    @Column(name = "field")
    private String field;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;
}
