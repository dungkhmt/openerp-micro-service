package openerp.openerpresourceserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "code_plagiarism")
public class CodePlagiarism implements Serializable {
    @Id
    @Column(name = "plagiarism_id",updatable = false, nullable = false)
    private String id;

    @Column(name = "contest_id")
    private String contestId;

    @Column(name = "problem_id")
    private String problemId;

    @Column(name = "user_id_1")
    private String user1;

    @Column(name = "user_id_2")
    private String user2;

    @Column(name = "submission_id1")
    private String submissionId1;

    @Column(name = "submission_id2")
    private String submissionId2;
}
