package com.hust.baseweb.applications.education.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "edu_course_chapter")
public class EduCourseChapter {

    public static final String STATUS_PRIVATE = "STATUS_PRIVATE";
    public static final String STATUS_PUBLIC = "STATUS_PUBLIC";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "chapter_id")
    private UUID chapterId;

    @Column(name = "chapter_name")
    private String chapterName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private EduCourse eduCourse;

    @Column(name = "status_id")
    private String statusId;

}
