package openerp.openerpresourceserver.programmingcontest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "programming_contest_problem_ranking")
@IdClass(CompositeProgrammingContestProblemRankingId.class)
public class ProgrammingContestProblemRanking {
    @Id
    @Column(name = "user_id")
    private String userId;
    @Column(name="user_fullname")
    private String userFullname;

    @Id
    @Column(name="contest_id")
    private String contestId;

    @Column(name="contest_name")
    private String contestName;

    @Id
    @Column(name="problem_id")
    private String problemId;

    @Column(name="problem_name")
    private String problemName;


    @Column(name="point")
    private Long point;

    @Column(name="status")
    private String status;

    @Column(name="last_updated_stamp")
    private Date lastUpdatedStamp;


    @Column(name="created_stamp")
    private Date createdStamp;

}
