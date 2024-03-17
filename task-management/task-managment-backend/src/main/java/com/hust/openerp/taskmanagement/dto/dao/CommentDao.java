package com.hust.openerp.taskmanagement.dto.dao;

import com.hust.openerp.taskmanagement.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
@Deprecated
public class CommentDao {
    private UUID id;
    private UUID taskId;
    private String createdByUserId;
    private String comment;
    private boolean status;
    private boolean isModify;
    private String createdDate;

    public CommentDao(Comment comment, boolean isModify) {
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        this.setId(comment.getCommentId());
        this.setTaskId(comment.getTaskId());
        this.setStatus(comment.isStatus());
        this.setModify(isModify);
        this.setCreatedByUserId(comment.getCreatorId());
        this.setComment(comment.getContent());
        this.setCreatedDate(comment.getCreatedStamp() != null ? sdf.format(comment.getCreatedStamp()) : null);
    }

}
