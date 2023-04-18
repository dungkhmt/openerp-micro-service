package com.hust.baseweb.applications.education.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;
import java.util.*;
@Getter
@Setter
@Entity
@Table(name = "quiz_question_user_role")

public class QuizQuestionUserRole {
    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_VIEW = "VIEW";

    public static List<String> getRoles(){
        List<String> lst = new ArrayList();
        lst.add(ROLE_OWNER);
        lst.add(ROLE_MANAGER);
        lst.add(ROLE_VIEW);
        return lst;
    }
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="question_id")
    private UUID questionId;

    @Column(name="user_id")
    private String userId;

    @Column(name="role_id")
    private String roleId;

    @Column(name="created_stamp")
    private Date createdStamp;
}
