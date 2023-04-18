package com.hust.baseweb.applications.programmingcontest.entity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user_contest_problem_role")

public class UserContestProblemRole {
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_VIEW = "VIEW";
    public static final String ROLE_OWNER = "OWNER";

    public static List<String> getListRoles(){
        List<String> L = new ArrayList();
        L.add(ROLE_MANAGER);
        L.add(ROLE_VIEW);
        L.add(ROLE_OWNER);
        return L;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="user_id")
    private String userId;

    @Column(name="problem_id")
    private String problemId;

    @Column(name="role_id")
    private String roleId;

    @Column(name="update_by_user_id")
    private String updateByUserId;

    @Column(name="created_stamp")
    private Date createdStamp;

    @Column(name="last_updated_stamp")
    private Date lastUpdated;

}
