package com.hust.baseweb.applications.exam.entity;

import com.hust.baseweb.applications.exam.utils.SecurityUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@MappedSuperclass
@FieldNameConstants
@NoArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Id
    @Column(length = 60)
    protected String id;

    @Column(name = "created_by")
    protected String createdBy;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;

    @Column(name = "updated_by")
    protected String updatedBy;

    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        createdBy = SecurityUtils.getUserLogin();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updatedBy = SecurityUtils.getUserLogin();
    }

}
