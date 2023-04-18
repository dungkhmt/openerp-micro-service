package com.hust.baseweb.applications.taskmanagement.dto.form;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommentForm {
    private String comment;
    private UUID projectId;
}
