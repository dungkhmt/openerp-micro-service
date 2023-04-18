package com.hust.baseweb.applications.taskmanagement.dto.dao;

import com.hust.baseweb.applications.taskmanagement.entity.Comment;
import com.hust.baseweb.applications.taskmanagement.entity.TaskExecution;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class CommentDao {
    private UUID id;
    private UUID taskId;
    private String createdByUserId;
    private String comment;
    private boolean status;
    private boolean isModify;
    private String createdDate;

    public CommentDao(Comment comment, boolean isModify){
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        this.setId(comment.getCommentId());
        this.setTaskId(comment.getTaskId());
        this.setStatus(comment.isStatus());
        this.setModify(isModify);
        this.setCreatedByUserId(comment.getCreatedByUserLoginId());
        this.setComment(comment.getComment());
        this.setCreatedDate(comment.getCreatedStamp() != null ? sdf.format(comment.getCreatedStamp()) : null);
    }


}
