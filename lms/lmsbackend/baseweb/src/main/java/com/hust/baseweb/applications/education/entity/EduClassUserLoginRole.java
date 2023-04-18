package com.hust.baseweb.applications.education.entity;

import com.hust.baseweb.applications.education.entity.compositeid.EduClassUserLoginRoleId;
import com.hust.baseweb.entity.UserLogin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="edu_class_user_login_role")
@IdClass(EduClassUserLoginRoleId.class)
public class EduClassUserLoginRole {
    public static final String ROLE_PARTICIPANT = "PARTICIPANT";
    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_MANAGER = "MANAGER";

    @Id
    @Column(name="class_id")
    private UUID classId;

    @Id
    @Column(name="user_login_id")
    private String userLoginId;

    @Id
    @Column(name="role_id")
    private String roleId;

    @Id
    @Column(name="from_date")
    private Date fromDate;

    @Column(name="thru_date")
    private Date thruDate;

    /*
    @ManyToOne
    @JoinColumn(name="user_login_id", referencedColumnName="user_login_id")
    private UserLogin userLogin;

    @ManyToOne
    @JoinColumn(name="class_id", referencedColumnName = "id")
    private EduClass eduClass;
    */
}
