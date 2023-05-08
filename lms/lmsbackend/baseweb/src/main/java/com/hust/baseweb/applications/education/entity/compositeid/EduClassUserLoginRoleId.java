package com.hust.baseweb.applications.education.entity.compositeid;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EduClassUserLoginRoleId implements Serializable {
    private UUID classId;
    private String userLoginId;
    private String roleId;
    private Date fromDate;
}
