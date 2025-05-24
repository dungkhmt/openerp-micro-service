package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster.ShiftDefinition;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils.JpaMapConverter;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils.JpaShiftDefinitionListConverter;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "hr_roster_template")
public class RosterTemplateEntity extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Convert(converter = JpaShiftDefinitionListConverter.class)
    @Column(name = "defined_shifts")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<ShiftDefinition> definedShifts;

    @Convert(converter = JpaMapConverter.class)
    @Column(name = "active_hard_constraints")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> activeHardConstraints;

    @Column(name = "departments", columnDefinition = "text[]")
    @Type(ListArrayType.class)
    private List<String> departments;

    @Column(name = "job_positions", columnDefinition = "text[]")
    @Type(ListArrayType.class)
    private List<String> jobPositions;

}