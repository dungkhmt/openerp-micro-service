package com.hust.openerp.taskmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "task_management_skill")
public class Skill {

    @Id
    @Column(name = "id")
    private String skillId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

}
