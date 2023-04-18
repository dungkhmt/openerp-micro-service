package com.hust.baseweb.applications.education.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "comment_on_quiz_question")
public class CommentOnQuizQuestion {
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_DISABLED = "DISABLED";

    @Id
    @Column(name="comment_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID commentId;

    @Column(name="question_id")
    private UUID questionId;

    @Column(name="created_by_user_login_id")
    private String createdByUserLoginId;

    @Column(name="comment_text")
    private String commentText;

    @Column(name="reply_to_comment_id")
    private UUID replyToCommentId;

    @Column(name="status_id")
    private String statusId;

    @Column(name="created_stamp")
    private Date createdStamp;

}
