package com.hust.openerp.taskmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationUserDTO {
    private UserDTO user;
    private UUID organizationId;
    private Date fromDate;
    private Date thrsDate;
    private String roleId;
}
