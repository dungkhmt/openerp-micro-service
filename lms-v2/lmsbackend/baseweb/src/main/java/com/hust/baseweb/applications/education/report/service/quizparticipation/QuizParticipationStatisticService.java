package com.hust.baseweb.applications.education.report.service.quizparticipation;

import com.hust.baseweb.applications.education.report.model.quizparticipation.GetQuizParticipationStatisticInputModel;
import com.hust.baseweb.applications.education.report.model.quizparticipation.QuizParticipationStatisticOutputModel;

import java.util.List;

public interface QuizParticipationStatisticService {

    public List<QuizParticipationStatisticOutputModel> getQuizParticipationStatistic(
        GetQuizParticipationStatisticInputModel input
    );
}
