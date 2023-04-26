package com.hust.baseweb.applications.admin.dataadmin.education.repo;

import com.hust.baseweb.applications.admin.dataadmin.education.model.statistic.QuizDoingTimeModel;
import com.hust.baseweb.applications.admin.dataadmin.education.model.statistic.TotalQuizDoingTimeModel;
import com.hust.baseweb.applications.education.entity.LogUserLoginQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface DoingPracticeQuizStatisticRepo extends JpaRepository<LogUserLoginQuizQuestion, UUID> {

    @Query(
        nativeQuery = true,
        value =
        "SELECT lg.user_login_id as loginId, COUNT(*) as totalQuizDoingTimes " +
        "FROM log_user_login_quiz_question lg " +
        "WHERE lg.created_stamp > :statisticFrom " +
        "GROUP BY lg.user_login_id"
    )
    List<TotalQuizDoingTimeModel> countTotalQuizDoingTimes(@Param("statisticFrom") Date statisticFrom);

    @Query(
        "SELECT lg.userLoginId as loginId, MAX(lg.createStamp) as doingTime " +
        "FROM LogUserLoginQuizQuestion lg " +
        "WHERE lg.createStamp > :statisticFrom " +
        "GROUP BY lg.userLoginId"
    )
    List<QuizDoingTimeModel> findLatestTimesDoingQuiz(@Param("statisticFrom") Date statisticFrom);

    @Query(
        "SELECT lg.userLoginId as loginId, lg.createStamp as doingTime " +
        "FROM LogUserLoginQuizQuestion lg " +
        "WHERE lg.createStamp > :statisticFrom " +
        "ORDER BY lg.createStamp ASC"
    )
    List<QuizDoingTimeModel> findDoingTimesSortAsc(@Param("statisticFrom") Date statisticFrom);

    @Query(
        "SELECT lg.userLoginId as loginId, MAX(lg.createStamp) as doingTime " +
        "FROM LogUserLoginQuizQuestion lg " +
        "WHERE lg.userLoginId IN :loginIds AND lg.createStamp <= :maxDoingTime " +
        "GROUP BY lg.userLoginId"
    )
    List<QuizDoingTimeModel> findLatestDoingTimesBefore(@Param("maxDoingTime") Date maxDoingTime,
                                                        @Param("loginIds") Set<String> loginIds);
}
