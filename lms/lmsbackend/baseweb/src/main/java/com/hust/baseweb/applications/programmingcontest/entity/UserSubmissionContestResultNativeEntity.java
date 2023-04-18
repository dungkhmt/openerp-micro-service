package com.hust.baseweb.applications.programmingcontest.entity;

import lombok.*;
import com.hust.baseweb.applications.programmingcontest.composite.UserSubmissionContestResultID;
import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
//@Table(name = "user_submission_contest_result")
@Table(name = "user_submission_contest_result_new")
@IdClass(UserSubmissionContestResultID.class)
public class UserSubmissionContestResultNativeEntity {
    @Id
    @Column(name = "contest_id")
    private String contestId;

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "point")
    private int point;

    @Column(name = "email")
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "created_stamp")
    private Date createdAt;

    @Column(name = "last_updated_stamp")
    private Date updateAt;
}
