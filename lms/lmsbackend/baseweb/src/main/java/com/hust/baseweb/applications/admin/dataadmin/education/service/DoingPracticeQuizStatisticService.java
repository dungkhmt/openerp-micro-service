package com.hust.baseweb.applications.admin.dataadmin.education.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public interface DoingPracticeQuizStatisticService {

    Date findLatestStatisticTime();
    Map<String, Long> statisticTotalQuizDoingTimes(Date statisticFrom);

    Map<String, LocalDateTime> statisticLatestTimeDoingQuiz(Date statisticFrom);

    Map<String, Long> statisticNumberOfQuizDoingPeriods(Date statisticFrom, int hoursBetweenPeriods);
}
