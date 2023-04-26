package com.hust.baseweb.applications.education.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "edu_course_chapter_material")
public class EduCourseChapterMaterial {

    public static final String EDU_COURSE_MATERIAL_TYPE_VIDEO = "EDU_COURSE_MATERIAL_TYPE_VIDEO";
    public static final String EDU_COURSE_MATERIAL_TYPE_SLIDE = "EDU_COURSE_MATERIAL_TYPE_SLIDE";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID eduCourseMaterialId;

    @Column(name = "edu_course_material_name")
    private String eduCourseMaterialName;

    @Column(name = "edu_course_material_type")
    private String eduCourseMaterialType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", referencedColumnName = "chapter_id")
    private EduCourseChapter eduCourseChapter;

    @Column(name = "source_id")
    private UUID sourceId;

    @Column(name = "slide_id")
    private String slideId;


}
