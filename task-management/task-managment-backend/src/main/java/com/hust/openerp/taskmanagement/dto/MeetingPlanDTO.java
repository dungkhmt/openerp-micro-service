package com.hust.openerp.taskmanagement.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.hust.openerp.taskmanagement.entity.Status;
import com.hust.openerp.taskmanagement.entity.User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MeetingPlanDTO {
    private UUID id;
    private String name;
    private String description;
    private Date createdStamp;
    private String createdBy;
    private User creator;
    private Date registrationDeadline;
    private String location;
    private Status status;
    private String statusId;
    private MeetingSessionDTO assignedSession;
    private List<User> members;
}