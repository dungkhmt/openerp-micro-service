package com.hust.baseweb.applications.admin.dataadmin.education.service;

import com.hust.baseweb.applications.admin.dataadmin.education.model.statistic.CodeSubmissionTimeModel;
import com.hust.baseweb.applications.admin.dataadmin.education.model.statistic.TotalCodeSubmissionModel;
import com.hust.baseweb.applications.admin.dataadmin.education.repo.ProgrammingContestStatisticRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProgrammingContestStatisticServiceImpl implements ProgrammingContestStatisticService {

    private final ProgrammingContestStatisticRepo programmingContestStatisticRepo;

    @Override
    public Map<String, Long> statisticTotalSubmissions(Date statisticFrom) {
        List<TotalCodeSubmissionModel> listTotalSubmissions = programmingContestStatisticRepo.countTotalCodeSubmissions(statisticFrom);

        return listTotalSubmissions.stream().collect(
            Collectors.toMap(elem -> elem.getLoginId(), elem -> elem.getTotalSubmissions())
        );
    }

    @Override
    public Map<String, LocalDateTime> statisticLatestTimesSubmittingCode(Date statisticFrom) {
        List<CodeSubmissionTimeModel> submissionTimes = programmingContestStatisticRepo.findLatestTimesSubmittingCode(statisticFrom);

        return submissionTimes.stream().collect(
            Collectors.toMap(elem -> elem.getLoginId(), elem -> elem.getSubmitTime())
        );
    }

    private static final List<String> ACCEPT_STATUSES = Arrays.asList(
        SUBMISSION_STATUS_ACCEPTED,
        SUBMISSION_STATUS_PARTIAL
    );

    @Override
    public Map<String, Long> statisticTotalSubmissionsAcceptedOnTheFirstSubmit(Date statisticFrom) {
        List<TotalCodeSubmissionModel> totalSubmissionsAcceptedFirstSubmit = programmingContestStatisticRepo.countSubmissionsHasFirstSubmitStatusIn(
            statisticFrom, ACCEPT_STATUSES
        );

        return totalSubmissionsAcceptedFirstSubmit.stream().collect(
            Collectors.toMap(elem -> elem.getLoginId(), elem -> elem.getTotalSubmissions())
        );
    }

    private static final List<String> ERROR_STATUSES = Arrays.asList(
        SUBMISSION_STATUS_FAILED,
        SUBMISSION_STATUS_WRONG,
        SUBMISSION_STATUS_TIME_LIMIT_EXCEEDED,
        SUBMISSION_STATUS_OUTPUT_LIMIT_EXCEEDED,
        SUBMISSION_STATUS_MEMORY_ALLOCATION_ERROR,
        SUBMISSION_STATUS_COMPILE_ERROR
    );

    @Override
    public Map<String, Long> statisticTotalErrorSubmissions(Date statisticFrom) {
        List<TotalCodeSubmissionModel> totalErrorSubmissions = programmingContestStatisticRepo.countTotalCodeSubmissionsHasStatusIn(
            statisticFrom, ERROR_STATUSES
        );

        return totalErrorSubmissions.stream().collect(
            Collectors.toMap(elem -> elem.getLoginId(), elem -> elem.getTotalSubmissions())
        );
    }
}
