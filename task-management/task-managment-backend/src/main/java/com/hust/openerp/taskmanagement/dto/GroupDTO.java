package com.hust.openerp.taskmanagement.dto;

import com.hust.openerp.taskmanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private UUID id;
    private String name;
    private String description;
    private UUID organizationId;
    private String createdBy;
    private Date createdStamp;
    private List<User> members;
}

