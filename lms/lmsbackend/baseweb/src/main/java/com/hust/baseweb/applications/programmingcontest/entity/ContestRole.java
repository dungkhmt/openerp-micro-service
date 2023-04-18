package com.hust.baseweb.applications.programmingcontest.entity;

import com.hust.baseweb.applications.programmingcontest.composite.ContestUserLoginRoleFromDateId;
import com.hust.baseweb.applications.programmingcontest.composite.UserSubmissionContestResultID;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "contest_role")
@IdClass(ContestUserLoginRoleFromDateId.class)

public class ContestRole {
    public static final String CONTEST_ROLE_OWNER = "OWNER";
    public static final String CONTEST_ROLE_MANAGER = "MANAGER";
    public static final String CONTEST_ROLE_PARTICIPANT = "PARTICIPANT";

    @Id
    @Column(name="contest_id")
    private String contestId;

    @Id
    @Column(name="user_login_id")
    private String userLoginId;

    @Id
    @Column(name="role_id")
    private String roleId;

    @Id
    @Column(name="from_date")
    private Date fromDate;

    @Column(name="thru_date")
    private Date thruDate;


}
