package com.hust.baseweb.applications.admin.dataadmin.education.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public interface ProgrammingContestStatisticService {

    Map<String, Long> statisticTotalSubmissions(Date statisticFrom);

    Map<String, LocalDateTime> statisticLatestTimesSubmittingCode(Date statisticFrom);

    Map<String, Long> statisticTotalSubmissionsAcceptedOnTheFirstSubmit(Date statisticFrom);

    Map<String, Long> statisticTotalErrorSubmissions(Date statisticFrom);
}
