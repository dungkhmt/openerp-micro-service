package com.hust.baseweb.applications.education.model.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentOnQuizQuestionDetailOM {
    private UUID commentId;
    private UUID questionId;
    private String commentText;
    private String createdByUserLoginId;
    private String fullNameOfCreator;
    private Date createdStamp;
    private UUID replyToCommentId;
}
