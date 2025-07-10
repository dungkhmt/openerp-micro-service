package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.SubmissionHourlySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SubmissionHourlySummaryRepo extends JpaRepository<SubmissionHourlySummary, String> {

    @Query(value = "WITH subquery AS ( " +
            "    SELECT " +
            "        user_submission_id, " +
            "        hour_of_day AS range_hour, " +
            "        SUM(shs.submission_pass_count) + (COALESCE(( " +
            "                SELECT SUM(submission_pass_count)  " +
            "                FROM submission_hourly_summary  " +
            "                WHERE hour_of_day = shs.hour_of_day+1 AND user_submission_id = shs.user_submission_id), 0) ) AS total_pass_submissions, " +
            "        SUM(shs.submission_count) + (COALESCE(( " +
            "                SELECT SUM(submission_count)  " +
            "                FROM submission_hourly_summary  " +
            "                WHERE hour_of_day = shs.hour_of_day+1 AND user_submission_id = shs.user_submission_id), 0) ) AS total_submissions " +
            "    FROM  " +
            "        submission_hourly_summary shs " +
            "    WHERE " +
            "        shs.user_submission_id = :userId " + // Điều kiện WHERE cho userId
            "    GROUP BY  " +
            "        user_submission_id, range_hour " +
            "    ORDER BY     " +
            "        user_submission_id, range_hour " +
            "), " +
            "max_submissions AS ( " +
            "    SELECT  " +
            "        user_submission_id, " +
            "        MAX(total_submissions) AS max_total_submissions " +
            "    FROM  " +
            "        subquery " +
            "    GROUP BY  " +
            "        user_submission_id " +
            "), " +
            "max_pass_submissions AS ( " +
            "    SELECT  " +
            "        user_submission_id, " +
            "        MAX(total_pass_submissions) AS max_total_pass_submissions " +
            "    FROM  " +
            "        subquery " +
            "    GROUP BY  " +
            "        user_submission_id " +
            "), " +
            "combined_max AS ( " +
            "        SELECT  " +
            "            COALESCE(ms.user_submission_id, mps.user_submission_id) AS user_submission_id, " +
            "            ms.max_total_submissions, " +
            "            mps.max_total_pass_submissions " +
            "        FROM  " +
            "            max_submissions ms " +
            "        FULL OUTER JOIN  " +
            "            max_pass_submissions mps ON ms.user_submission_id = mps.user_submission_id " +
            ") " +
            "SELECT  " +
            "    cm.user_submission_id, " +
            "    max_total_submissions, " +
            "    max_total_pass_submissions, " +
            "    CONCAT( " +
            "        LPAD(TO_CHAR(sm.range_hour, 'FM00'), 2, '0'), 'h00 - ', " +
            "        LPAD(TO_CHAR(sm.range_hour + 2, 'FM00'), 2, '0'), 'h00' " +
            "    ) AS max_total_submissions_hour_range, " +
            "    CONCAT( " +
            "        LPAD(TO_CHAR(pm.range_hour, 'FM00'), 2, '0'), 'h00 - ', " +
            "        LPAD(TO_CHAR(pm.range_hour + 2, 'FM00'), 2, '0'), 'h00' " +
            "    ) AS max_total_pass_submissions_hour_range " +
            "FROM  " +
            "    combined_max cm " +
            "LEFT JOIN  " +
            "    subquery sm ON cm.user_submission_id = sm.user_submission_id AND cm.max_total_submissions = sm.total_submissions " +
            "LEFT JOIN  " +
            "    subquery pm ON cm.user_submission_id = pm.user_submission_id AND cm.max_total_pass_submissions = pm.total_pass_submissions " +
            "LIMIT 1 ", nativeQuery = true)
    Object findMaxTotalSubmissionsByUserId(@Param("userId") String userId);

    @Query(value =
            "SELECT " +
                    "    user_submission_id," +
                    "    CONCAT(\n" +
                    "        LPAD(TO_CHAR( MIN(hour_of_day), 'FM00'), 2, '0'), 'h00') AS startTime," +
                    "    CONCAT(\n" +
                    "        LPAD(TO_CHAR( MAX(hour_of_day), 'FM00'), 2, '0'), 'h00') AS endTime " +
                    "FROM " +
                    "    submission_hourly_summary  " +
                    "WHERE  " +
                    "    user_submission_id = :userId " +
                    "GROUP BY " +
                    "    user_submission_id", nativeQuery = true)
    Object findStartEndTimeSubmittedByUserId(@Param("userId") String userId);


    @Query(value =
            "SELECT " +
                    "    user_submission_id," +
                    "    CONCAT(\n" +
                    "        LPAD(TO_CHAR( MIN(hour_of_day), 'FM00'), 2, '0'), 'h00') AS startTime," +
                    "    CONCAT(\n" +
                    "        LPAD(TO_CHAR( MAX(hour_of_day), 'FM00'), 2, '0'), 'h00') AS endTime" +
                    "FROM " +
                    "    submission_hourly_summary  " +
                    "GROUP BY " +
                    "    user_submission_id", nativeQuery = true)

    Object[] findStartEndTimeSubmitted();

    @Query(value =
            "WITH AllHours AS (SELECT generate_series(0, 23) AS hour_of_day) " +
                    "SELECT AllHours.hour_of_day, " +
                    "       COALESCE(SUM(shs.submission_count), 0) AS total_submission_count, " +
                    "       COALESCE(SUM(shs.submission_pass_count), 0) AS total_submission_pass_count " +
                    "FROM AllHours " +
                    "LEFT JOIN submission_hourly_summary shs " +
                    "       ON AllHours.hour_of_day = shs.hour_of_day " +
                    "       AND shs.user_submission_id = :userId " +
                    "GROUP BY AllHours.hour_of_day " +
                    "ORDER BY AllHours.hour_of_day ", nativeQuery = true)
    Object[] submissionHourlySummariesByHourByUserID(@Param("userId") String userId);
}
