package com.hust.baseweb.applications.programmingcontest.composite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class ContestUserLoginRoleFromDateId implements Serializable {
    private String contestId;
    private String userLoginId;
    private String roleId;
    private Date fromDate;
}
