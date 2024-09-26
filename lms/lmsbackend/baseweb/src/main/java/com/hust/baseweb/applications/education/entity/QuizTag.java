package com.hust.baseweb.applications.education.entity;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "quiz_tags")
public class QuizTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    private UUID tagId;

    @Column(name = "course_id")
    private String courseId ;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;
}
