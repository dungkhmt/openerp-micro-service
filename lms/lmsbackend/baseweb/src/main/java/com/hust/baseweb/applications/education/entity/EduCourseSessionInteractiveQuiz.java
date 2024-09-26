package com.hust.baseweb.applications.education.entity;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "edu_course_session_interactive_quiz")
public class EduCourseSessionInteractiveQuiz {
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_OPENED = "OPENED";
    public static final String STATUS_CLOSED = "CLOSED";

    public static final String STATUS_CANCELLED = "CANCELLED";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "interactive_quiz_name")
    private String interactiveQuizName;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "description")
    private String description;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;
}
