package openerp.openerpresourceserver.service.impl;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.model.DashBoard;
import openerp.openerpresourceserver.repo.ContestSubmissionRepo;
import openerp.openerpresourceserver.repo.QuizQuestionRepo;
import openerp.openerpresourceserver.repo.UserRepo;
import openerp.openerpresourceserver.service.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
public class DashboardServiceImpl implements DashBoardService {

    private QuizQuestionRepo quizQuestionRepo;
    private ContestSubmissionRepo contestSubmissionRepo;
    private UserRepo userRepo;
    @Override
    public DashBoard getDashBoard() {
        DashBoard dashBoardData = new DashBoard();
        dashBoardData.setTotalQuizQuestion(quizQuestionRepo.count());
        dashBoardData.setTotalProblem(contestSubmissionRepo.countAllProblemIds());
        dashBoardData.setTotalSubmissions(contestSubmissionRepo.count());
        dashBoardData.setTotalUserActive(userRepo.count());
        dashBoardData.setTotalStudentPassBySemester(contestSubmissionRepo.findResultAllSemester());
        return dashBoardData;
    }
}
