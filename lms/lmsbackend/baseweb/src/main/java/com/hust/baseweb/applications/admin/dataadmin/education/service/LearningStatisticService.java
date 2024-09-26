package com.hust.baseweb.applications.admin.dataadmin.education.service;

import com.hust.baseweb.applications.admin.dataadmin.education.model.statistic.LearningStatisticResultsModel;
import org.springframework.data.domain.Pageable;

public interface LearningStatisticService {

    void statisticLearningGeneral();

    LearningStatisticResultsModel findLearningStatisticResults(String partOfLoginId, Pageable pageable);
}
