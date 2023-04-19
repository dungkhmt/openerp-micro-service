package com.hust.baseweb.applications.education.entity;

import com.hust.baseweb.entity.UserLogin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "edu_class")
@EntityListeners(AuditingEntityListener.class)
public class EduClass {
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_HIDDEN = "HIDDEN";
    public static final String STATUS_DISABLED = "DISABLED";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private Integer code;

    @Column(name = "class_code")
    private String classCode;

    @Column(name="status_id")
    private String statusId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "semester_id", referencedColumnName = "id")
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private EduCourse eduCourse;

    private String classType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private EduDepartment eduDepartment;

    @ManyToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "user_login_id")
    private UserLogin teacher;

    @LastModifiedDate
    private Date lastUpdatedStamp;

    @CreatedDate
    private Date createdStamp;

    @Transient
    private String message = "";
}
