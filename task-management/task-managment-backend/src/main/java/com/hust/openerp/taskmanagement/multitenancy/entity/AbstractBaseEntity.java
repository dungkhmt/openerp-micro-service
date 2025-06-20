package com.hust.openerp.taskmanagement.multitenancy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import org.hibernate.annotations.TenantId;

import java.io.Serial;
import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractBaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TenantId
    @Column(name = "organization_code")
    private String organizationCode;
}