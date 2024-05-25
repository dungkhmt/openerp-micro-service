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
//    So ngon ngu lap trinh co the su dung
    private int numberProgramLanguage;
//    So lan nop bai
    private int totalSubmitted;

//    So contest da submit
    private int totalContestSubmitted;

//    So bai da nop
    private int totalProblemSubmitted;

//    So bai da accept
    private int totalProblemSubmittedAccept;

//    trung binh so submission tren ngay
    private double averageSubmissionPerDay;

//    Trung binh bao nhieu lan submission de duoc accept
    private double averageMinimumSubmissionToAccept;

//    ty le nop lan dau tien duoc test
//    private double firstSubmissionAccuracyRate;

//    Lan dau tien nop bai
    private LocalDate firstSubmissionDate;

//    Lan cuoi nop bai
    private LocalDate lastSubmissionDate;

}
