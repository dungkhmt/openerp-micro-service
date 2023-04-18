package com.hust.baseweb.applications.education.model;

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
public class CommentEduCourseDetailOM {
    private UUID commentId;
    private UUID eduCourseMaterialId;
    private UUID replyToCommentId;
    private String commentMessage;
    private String postedByUserLoginId;
    private String fullNameOfCreator;
    private Date createdStamp;
}
