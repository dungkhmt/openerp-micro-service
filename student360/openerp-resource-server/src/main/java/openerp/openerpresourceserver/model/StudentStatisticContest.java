package openerp.openerpresourceserver.model;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StudentStatisticContest {
    private String studentId;
    private int totalSubmitted;
    private int totalProblemSubmitted;
    private int totalContestSubmitted;
    private int totalProblemSubmittedAccept;
    private int totalProblemSubmittedPartial;
    private int totalProblemSubmittedCompileError;
    private int numberProgramLanguage;
    private double averageMinimumSubmissionToAccept;
    private double averageSubmissionPerDay;
    private List<ContestSubmissionByStudent> contestDetails;
    private Map<LocalDate, Integer> dailySubmissionCounts;
    private List<StudentSubmissionBySemester> studentSubmissionBySemsters;
    private Object[] programmingLanguageSubmitCounts;
    private Object[] submissionHourlySummary;
    private Map<String, Integer> firstTimeAcceptList;


}
