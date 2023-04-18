package com.hust.baseweb.applications.admin.dataadmin.education.model.statistic;

import com.hust.baseweb.applications.admin.dataadmin.education.entity.LearningStatisticEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class LearningStatisticResultsModel {

    Date latestStatisticTime;

    Page<LearningStatisticEntity> statisticResults;

}
