package com.hust.baseweb.applications.education.report.service.quizparticipation;

import com.hust.baseweb.applications.education.entity.LogUserLoginQuizQuestion;
import com.hust.baseweb.applications.education.repo.LogUserLoginQuizQuestionRepo;
import com.hust.baseweb.applications.education.report.model.quizparticipation.GetQuizParticipationStatisticInputModel;
import com.hust.baseweb.applications.education.report.model.quizparticipation.QuizParticipationStatisticOutputModel;
import com.hust.baseweb.utils.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class QuizParticipationStatisticServiceImpl implements QuizParticipationStatisticService {

    private LogUserLoginQuizQuestionRepo logUserLoginQuizQuestionRepo;

    @Override
    public List<QuizParticipationStatisticOutputModel> getQuizParticipationStatistic(
        GetQuizParticipationStatisticInputModel input
    ) {
        List<LogUserLoginQuizQuestion> logUserLoginQuizQuestions = logUserLoginQuizQuestionRepo.findAll();
        List<QuizParticipationStatisticOutputModel> quizParticipationStatisticOutputModels = new ArrayList();
        HashMap<String, Integer> mDate2Count = new HashMap<>();

        int len = 10;
        if (input.getLength() != 0) {
            len = input.getLength();
        }

        for (LogUserLoginQuizQuestion i : logUserLoginQuizQuestions) {
            Date date = i.getCreateStamp();
            if (date == null) {
                continue;
            }
            String s_date = DateTimeUtils.date2YYYYMMDD(date);
            if (mDate2Count.get(s_date) == null) {
                mDate2Count.put(s_date, 1);
            } else {
                mDate2Count.put(s_date, mDate2Count.get(s_date) + 1);
            }

        }
        String[] s = new String[mDate2Count.keySet().size()];
        int idx = -1;
        for (String k : mDate2Count.keySet()) {
            idx++;
            s[idx] = k;
        }
        for (int i = 0; i < s.length; i++) {
            for (int j = i + 1; j < s.length; j++) {
                if (s[i].compareTo(s[j]) < 0) {
                    String tmp = s[i];
                    s[i] = s[j];
                    s[j] = tmp;
                }
            }
        }
        if (len > s.length) {
            len = s.length;
        }
        int acc = 0;
        for (int i = 0; i < len; i++) {
            String sd = s[i];
            int count = mDate2Count.get(sd);
            acc = acc + count;
        }
        for (int i = 0; i < len; i++) {
            String sd = s[i];
            int count = mDate2Count.get(sd);
            acc = acc - count;
            //for(String sd: mDate2Count.keySet()){
            quizParticipationStatisticOutputModels.add(new QuizParticipationStatisticOutputModel(
                sd,
                count, acc));
            //log.info("getQuizParticipationStatistic, map date " + sd + " -> " + mDate2Count.get(sd));
        }
        return quizParticipationStatisticOutputModels;
    }
}
