package com.hust.openerp.taskmanagement.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class SkillDTO {
    private UUID skillId;
    private String code;
    private String name;
    private String description;
    private Date createdStamp;
    private UUID organizationId;
}
