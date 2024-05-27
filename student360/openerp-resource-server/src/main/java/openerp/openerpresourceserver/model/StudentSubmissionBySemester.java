package openerp.openerpresourceserver.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudentSubmissionBySemester {
    private String userSubmissionId;
    private String semester;
    private int submissionMonth;
    private Long numberOfSubmissions;
    private double submissionDeviationByMonth;
}
