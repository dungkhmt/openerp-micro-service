package openerp.openerpresourceserver.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DashBoard {
    private long totalSubmissions;
    private long totalUserActive;
    private long totalProblem;
    private long totalQuizQuestion;
    private Object[] totalStudentPassBySemester;
    private Object[] totalStudentPlagiarismBySemester;
    private List<SemesterScore> semesterScores;
}