package com.hust.baseweb.applications.taskmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "backlog_user_login_skill")
public class UserloginSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "backlog_user_login_skill_id")
    private UUID id;

    @Column(name = "user_login_id")
    private String userLoginId;

    @Column(name = "backlog_skill_id")
    private String backlogSkillId;
}
