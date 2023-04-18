package com.hust.baseweb.applications.education.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "log_user_login_course_chapter_material")
public class LogUserLoginCourseChapterMaterial {

    @Id
    @Column(name = "user_login_course_chapter_material_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID userLoginCourseChapterMaterial;

    @Column(name = "user_login_id")
    private String userLoginId;

    @Column(name = "edu_course_material_id")
    private UUID eduCourseMaterialId;

    @Column(name = "created_stamp")
    private Date createStamp;

    @ManyToOne
    @JoinColumn(name="edu_class_id")
    private EduClass eduClass;
}
