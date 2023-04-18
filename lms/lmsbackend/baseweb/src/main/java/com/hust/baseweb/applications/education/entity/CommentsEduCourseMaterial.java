package com.hust.baseweb.applications.education.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CommentsEduCourseMaterial {

    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_DISABLED = "DISABLED";

    @Id
    @Column(name="comment_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID commentId;

    @Column(name="edu_course_material_id")
    private UUID eduCourseMaterialId;

    @Column(name="posted_by_user_login_id")
    private String postedByUserLoginId;

    @Column(name="comment_message")
    private String commentMessage;

    @Column(name="reply_to_comment_id")
    private UUID replyToCommentId;

    @Column(name="status_id")
    private String statusId;

    @Column(name="created_stamp")
    private Date createdStamp;

}
