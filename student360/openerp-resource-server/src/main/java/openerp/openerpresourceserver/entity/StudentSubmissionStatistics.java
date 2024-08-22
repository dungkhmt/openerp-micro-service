package openerp.openerpresourceserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_submission_statistics")
public class StudentSubmissionStatistics implements Serializable {
    @Id
    private String studentId;
    private int numberProgramLanguage;
    private int totalSubmitted;
    private int totalContestSubmitted;
    private int totalProblemSubmitted;
    private int totalProblemSubmittedAccept;
    private double averageSubmissionPerDay;
    private double averageMinimumSubmissionToAccept;
    private double firstSubmissionAccuracyRate;
    private LocalDate firstSubmissionDate;
    private LocalDate lastSubmissionDate;

}
