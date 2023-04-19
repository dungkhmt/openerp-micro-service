package com.hust.baseweb.applications.education.model.educlassuserloginrole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddEduClassUserLoginRoleIM {
    private UUID classId;
    private String userLoginId;
    private String roleId;
}
