package com.hust.baseweb.applications.education.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "quiz_course_topic")
public class QuizCourseTopic {

    @Id
    @Column(name = "quiz_course_topic_id")
    private String quizCourseTopicId;

    @Column(name = "quiz_course_topic_name")
    private String quizCourseTopicName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private EduCourse eduCourse;

    @Transient
    private String message = "";
}
