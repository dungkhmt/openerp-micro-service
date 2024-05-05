package openerp.openerpresourceserver.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudentSemesterResult {
    private String semester;
    private String userSubmissionId;
    private Long totalSubmission;
    private Long totalProblem;
    private int appearedInPlagiarism;
    private double midtermPoint;
    private double finalPoint;
    private int passingState;
    private double passingRate;
}
