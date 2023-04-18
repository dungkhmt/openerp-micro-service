package com.hust.baseweb.applications.taskmanagement.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "backlog_skill")
public class Skill {

    @Id
    @Column(name = "backlog_skill_id")
    private String skillId;

    @Column(name = "skill_name")
    private String name;

    @Column(name = "skill_description")
    private String description;

}
