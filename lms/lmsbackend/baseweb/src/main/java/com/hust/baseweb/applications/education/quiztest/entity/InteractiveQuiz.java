package com.hust.baseweb.applications.education.quiztest.entity;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "interactive_quiz")
public class InteractiveQuiz {
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_OPENED = "OPENED";
    public static final String STATUS_CLOSED = "CLOSED";

    public static final String STATUS_CANCELLED = "CANCELLED";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "interactive_quiz_id")
    private UUID interactive_quiz_id;

    @Column(name = "interactive_quiz_name")
    private String interactive_quiz_name;

    @Column(name = "session_id")
    private UUID sessionId;

    @Column(name = "status_id")
    private String statusId;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;
}
