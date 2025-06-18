package com.hust.openerp.taskmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {
    private UUID id;
    private String code;
    private String name;
    private String createdBy;
    private String createdStamp;
    private Integer memberCount;
    private String myRole;
}
