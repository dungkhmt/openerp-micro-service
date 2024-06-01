package openerp.openerpresourceserver.service.impl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.model.DashBoard;
import openerp.openerpresourceserver.model.SemesterScore;
import openerp.openerpresourceserver.repo.CodePlagiarismRepo;
import openerp.openerpresourceserver.repo.ContestSubmissionRepo;
import openerp.openerpresourceserver.repo.QuizQuestionRepo;
import openerp.openerpresourceserver.repo.UserRepo;
import openerp.openerpresourceserver.service.DashBoardService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class DashboardServiceImpl implements DashBoardService {

    private QuizQuestionRepo quizQuestionRepo;
    private ContestSubmissionRepo contestSubmissionRepo;
    private UserRepo userRepo;
    private CodePlagiarismRepo codePlagiarismRepo;

    @Override
    public DashBoard getDashBoard() {
        Object[] totalStudentPlagiarismBySemester = codePlagiarismRepo.countStudentPlagiarismBySemester();
        Object[] scoreBySemester = contestSubmissionRepo.numberScoreBySemester();
        Map<String, SemesterScore> semesterScoreMap = new LinkedHashMap<>();

        for (Object obj : scoreBySemester) {
            Object[] row = (Object[]) obj;
            String semester = (String) row[0];
            String evaluationResult = (String) row[1];
            long numStudents = (long) row[2];

            SemesterScore semesterScore = semesterScoreMap.getOrDefault(semester, new SemesterScore());
            semesterScore.setSemester(semester);
            switch (evaluationResult) {
                case "A/A+" -> semesterScore.setNumStudentsWithGradeA( numStudents);
                case "B/B+" -> semesterScore.setNumStudentsWithGradeB(numStudents);
                case "C/C+" -> semesterScore.setNumStudentsWithGradeC(numStudents);
                case "D/D+" -> semesterScore.setNumStudentsWithGradeD(numStudents);
                default -> semesterScore.setNumStudentsWithGradeF(numStudents);
            }
            semesterScoreMap.put(semester, semesterScore);
            System.out.println(semesterScoreMap);
        }

        List<SemesterScore> semesterScores = getSemesterScores(semesterScoreMap);

        DashBoard dashBoardData = new DashBoard();
        dashBoardData.setTotalQuizQuestion(quizQuestionRepo.count());
        dashBoardData.setTotalProblem(contestSubmissionRepo.countAllProblemIds());
        dashBoardData.setTotalSubmissions(contestSubmissionRepo.count());
        dashBoardData.setTotalUserActive(userRepo.count());
        dashBoardData.setTotalStudentPassBySemester(contestSubmissionRepo.findResultAllSemester());
        dashBoardData.setTotalStudentPlagiarismBySemester(totalStudentPlagiarismBySemester);
        dashBoardData.setSemesterScores(semesterScores);
        return dashBoardData;
    }

    @NotNull
    private List<SemesterScore> getSemesterScores(Map<String, SemesterScore> semesterScoreMap) {
        List<SemesterScore> semesterScores = new ArrayList<>(semesterScoreMap.values());

        for (int i = 0; i < semesterScores.size()-1; i++) {
            SemesterScore currentSemester = semesterScores.get(i);
            SemesterScore previousSemester = semesterScores.get(i + 1);

            currentSemester.setGrowthGradeA(calculateGrowthRate(currentSemester.getNumStudentsWithGradeA(), previousSemester.getNumStudentsWithGradeA()));
            currentSemester.setGrowthGradeB(calculateGrowthRate(currentSemester.getNumStudentsWithGradeB(), previousSemester.getNumStudentsWithGradeB()));
            currentSemester.setGrowthGradeC(calculateGrowthRate(currentSemester.getNumStudentsWithGradeC(), previousSemester.getNumStudentsWithGradeC()));
            currentSemester.setGrowthGradeD(calculateGrowthRate(currentSemester.getNumStudentsWithGradeD(), previousSemester.getNumStudentsWithGradeD()));
            currentSemester.setGrowthGradeF(calculateGrowthRate(currentSemester.getNumStudentsWithGradeF(), previousSemester.getNumStudentsWithGradeF()));
        }
        return semesterScores;
    }

    private double calculateGrowthRate(long currentNumStudents, long previousNumStudents) {
        if (previousNumStudents == 0) {
            return 0;
        }
        return ((double) currentNumStudents - previousNumStudents) / previousNumStudents * 100;
    }
}
