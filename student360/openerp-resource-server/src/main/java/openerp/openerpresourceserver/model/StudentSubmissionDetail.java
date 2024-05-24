package openerp.openerpresourceserver.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class StudentSubmissionDetail {
    private String studentId;

    private String email;

    private String fullname;

    private String midtermExamResult;

    private String finalExamResult;
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

//    So bai da dung 1 phan
    private int totalProblemSubmittedPartial;

//    So bai bi loi
    private int totalProblemSubmittedCompileError;

//    trung binh so submission tren ngay
    private double averageSubmissionPerDay;

//    Trung binh bao nhieu lan submission de duoc accept
    private double averageMinimumSubmissionToAccept;

//    ty le nop lan dau tien duoc test
    private double firstSubmissionAccuracyRate;

    private String startTimeActive;

    private String endTimeActive;

    private boolean hasProgress;

    private boolean hasHighScore;

    private Object[] submissionScoreHistory;

//    Lan dau tien nop bai
    private LocalDate firstSubmissionDate;

//    Lan cuoi nop bai
    private LocalDate lastSubmissionDate;

//    Khung gio nhieu submit nhat
    private String mostSubmittedTime;

//    Khung gio nhieu submit nhat
    private String mostEffectiveSubmittedTime;

    private boolean submittedMultipleTimes ;

//    cac contest da nop
    private List<ContestSubmissionByStudent> contestDetails;

//    so lan submission tren moi ngon ngu lap trinh
    private Object[] programmingLanguageSubmitCounts;

//    submit tren moi ngay
    private Map<LocalDate, Integer> dailySubmissionCounts;

//    lan dau tien duoc accept moi problem
    private Map<String, Integer> firstTimeAcceptList;

    // Số lượng submission theo mỗi giờ
    private Map<LocalTime, Integer> hourlySubmissionCounts;

    // Số lượng submission được điểm theo mỗi giờ
    private Map<LocalTime, Integer> hourlySubmissionPathCounts;

    private List<StudentSubmissionBySemster> studentSubmissionBySemsters;

    private String learningBehavior;

    private Object[] submissionHourlySummary;

    private List<StudentSemesterResult> studentSemesterResult;

    private int passState;
}
