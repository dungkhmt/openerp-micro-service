package com.hust.openerp.taskmanagement.dto;


import com.hust.openerp.taskmanagement.entity.Status;
import com.hust.openerp.taskmanagement.entity.User;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationInvitationDTO {
    private UUID id;
    private UUID organizationId;
    private String email;
    private String invitedBy;
    private String roleId;
    private String statusId;
    private String token;
    private Date expirationTime;
    private Date createdStamp;
    private Date lastUpdatedStamp;
    private OrganizationDTO organization;
    private User inviter;
    private Status status;
}
