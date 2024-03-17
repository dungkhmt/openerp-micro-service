package com.hust.openerp.taskmanagement.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_management_task_log")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLog {
  @Id
  @SequenceGenerator(name = "task_management_task_log_generator", sequenceName = "task_management_task_log_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "task_management_task_log_generator")
  @Column
  private Long id;

  @Column(name = "user_id")
  private String creatorId;

  @Column(name = "comment")
  private String comment;

  @Column(name = "task_id")
  private UUID taskId;

  @CreationTimestamp
  @Column(name = "created_at")
  private Date createdAt;

  @OneToMany(fetch = jakarta.persistence.FetchType.EAGER)
  @JoinColumn(name = "log_id")
  private List<TaskLogDetail> details;

  @ManyToOne(fetch = jakarta.persistence.FetchType.EAGER)
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User creator;
}
