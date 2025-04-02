package com.hust.openerp.taskmanagement.dto;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MeetingSessionDTO {
    private UUID id;
    private UUID planId;
    private Date startTime;
    private Date endTime;
    private Date createdStamp;
}