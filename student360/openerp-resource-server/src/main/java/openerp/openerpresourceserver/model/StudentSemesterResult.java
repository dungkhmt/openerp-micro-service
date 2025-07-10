package openerp.openerpresourceserver.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudentSemesterResult {
    private String userSubmissionId;
    private String semester;
    private double midtermPoint;
    private double finalPoint;
    private int appearedInPlagiarism;
    private int passingState;
}
