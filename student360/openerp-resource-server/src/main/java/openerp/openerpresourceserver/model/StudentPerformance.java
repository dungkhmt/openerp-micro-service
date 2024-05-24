package openerp.openerpresourceserver.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentPerformance {
    private String studentId;
    private int numberProgramLanguage;
    private int totalSubmitted;
    private int totalProblemSubmitted;
    private int passState;
    private double averageSubmissionPerDay;
    private double averageMinimumSubmissionToAccept;
    private double firstSubmissionAccuracyRate;
    private String startTimeActive;
    private String endTimeActive;
    private boolean hasProgress;
    private boolean hasHighScore;
    private String mostSubmittedTime;
    private String mostEffectiveSubmittedTime;
    private boolean submittedMultipleTimes ;
    private Object[] programmingLanguageSubmitCounts;
    private String learningBehavior;
    private List<StudentSemesterResult> studentSemesterResult;
}
