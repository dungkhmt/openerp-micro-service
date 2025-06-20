package com.hust.openerp.taskmanagement.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MeetingPlanUserDTO {
    private String userId;
    private UUID planId;
    private UUID sessionId;
}
