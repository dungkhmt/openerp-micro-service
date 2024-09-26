package com.hust.baseweb.applications.examclassandaccount.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exam_class_userlogin_map")
public class ExamClassUserloginMap {
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_DISABLE = "DISABLE";

    @javax.persistence.Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID Id;

    @Column(name="exam_class_id")
    private UUID examClassId;

    @Column(name="random_user_login_id")
    private String randomUserLoginId;

    @Column(name="password")
    private String password;

    @Column(name="real_user_login_id")
    private String realUserLoginId;

    @Column(name="code")
    private String studentCode;

    @Column(name="fullname")
    private String fullname;

    @Column(name="status")
    private String status;

}
