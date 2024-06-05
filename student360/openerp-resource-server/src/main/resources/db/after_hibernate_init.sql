DROP TABLE IF EXISTS "max_point_contest_view";
DROP TABLE IF EXISTS "midterm_final_submission_view";
DROP TABLE IF EXISTS "submission_hourly_summary";
DROP TABLE IF EXISTS "student_submission_statistics";

CREATE MATERIALIZED VIEW IF NOT EXISTS public.max_point_contest_view
AS SELECT ccpn.contest_id,
          sum(tsn.test_case_point) AS total_point
   FROM test_case_new tsn
            JOIN contest_contest_problem_new ccpn ON tsn.contest_problem_id::text = ccpn.problem_id::text
   WHERE upper(ccpn.contest_id::text) ~~ upper('%midterm%'::text) OR upper(ccpn.contest_id::text) ~~ upper('%final%'::text)
   GROUP BY ccpn.contest_id
   ORDER BY ccpn.contest_id;


CREATE MATERIALIZED VIEW IF NOT EXISTS public.midterm_final_submission_view
AS SELECT ranked.user_submission_id,
          ranked.contest_id,
          ranked.problem_id,
          ranked.semester,
          ranked.point
   FROM ( SELECT csn.user_submission_id,
                 csn.contest_id,
                 csn.problem_id,
                 CASE
                     WHEN date_part('month'::text, csn.created_stamp) >= 10::double precision THEN ('Kỳ '::text || date_part('year'::text, csn.created_stamp)) || '1'::text
                    WHEN date_part('month'::text, csn.created_stamp) <= 3::double precision THEN ('Kỳ '::text || (date_part('year'::text, csn.created_stamp) - 1::double precision)) || '1'::text
                    WHEN date_part('month'::text, csn.created_stamp) >= 4::double precision AND date_part('month'::text, csn.created_stamp) <= 8::double precision THEN ('Kỳ '::text || (date_part('year'::text, csn.created_stamp) - 1::double precision)) || '2'::text
                    ELSE NULL::text
                END AS semester,
            csn.point,
            row_number() OVER (PARTITION BY csn.user_submission_id, csn.contest_id, csn.problem_id, (
                CASE
                    WHEN date_part('month'::text, csn.created_stamp) >= 10::double precision THEN ('Kỳ '::text || date_part('year'::text, csn.created_stamp)) || '1'::text
                    WHEN date_part('month'::text, csn.created_stamp) <= 3::double precision THEN ('Kỳ '::text || (date_part('year'::text, csn.created_stamp) - 1::double precision)) || '1'::text
                    WHEN date_part('month'::text, csn.created_stamp) >= 4::double precision AND date_part('month'::text, csn.created_stamp) <= 8::double precision THEN ('Kỳ '::text || (date_part('year'::text, csn.created_stamp) - 1::double precision)) || '2'::text
                    ELSE NULL::text
                END) ORDER BY csn.point DESC) AS rn
          FROM contest_submission_new csn
          WHERE upper(csn.contest_id::text) ~~ upper('%midterm%'::text) OR upper(csn.contest_id::text) ~~ upper('%final%'::text)) ranked
   WHERE ranked.semester is not null and ranked.rn = 1;

CREATE MATERIALIZED VIEW IF NOT EXISTS submission_hourly_summary AS
SELECT
    date_trunc('day', created_stamp) AS submission_date,
    EXTRACT(hour FROM created_stamp) AS hour_of_day,
    COUNT(*) AS submission_count,
    SUM(CASE WHEN point > 0 THEN 1 ELSE 0 END) AS submission_pass_count,
    user_submission_id
FROM contest_submission_new
GROUP BY
    submission_date,
    hour_of_day,
    user_submission_id;

CREATE MATERIALIZED VIEW IF NOT EXISTS public.student_submission_statistics AS
WITH ranked_submissions AS (
    SELECT user_submission_id, problem_id, status,point,
           RANK() OVER (PARTITION BY user_submission_id, problem_id ORDER BY created_stamp) AS rank
    FROM contest_submission_new
),
first_attempt AS (
    SELECT user_submission_id, COUNT(problem_id) AS first_attempt_score_gt_zero
    FROM ranked_submissions t
    WHERE t.rank = 1 and t.point > 0
    GROUP BY user_submission_id
),

first_accept AS (
    SELECT
    user_submission_id,
    AVG(min_rank) AS average_min_rank
	FROM (
    SELECT
        user_submission_id,
        MIN(rank) AS min_rank
    FROM
        ranked_submissions
    WHERE
        status = 'Accept'
    GROUP BY
        user_submission_id, problem_id
) AS subquery
GROUP BY
    user_submission_id
)
SELECT
    s.user_submission_id AS student_id,
    COUNT(*) AS total_submitted,
    COUNT(DISTINCT s.problem_id) AS total_problem_submitted,
    COUNT(DISTINCT s.contest_id) AS total_contest_submitted,
    COUNT(DISTINCT
          CASE
              WHEN s.source_code_language LIKE '%CPP%' THEN 'CPP'
              WHEN s.source_code_language LIKE '%PYTHON%' THEN 'Python'
              WHEN s.source_code_language LIKE '%JAVA%' THEN 'Java'
              ELSE s.source_code_language
              END
    ) AS number_program_language,
    COUNT(DISTINCT CASE WHEN s.status = 'Accept' THEN s.problem_id END) AS total_problem_submitted_accept,
    CAST(COALESCE(fa.first_attempt_score_gt_zero, 0) AS DECIMAL(10, 2)) /  NULLIF(CAST(COUNT(DISTINCT s.problem_id) AS DECIMAL(10, 2)), 0) AS first_submission_accuracy_rate,
    coalesce(average_min_rank,0) as average_minimum_submission_to_accept,
    MIN(s.created_stamp) AS first_submission_date,
    MAX(s.created_stamp) AS last_submission_date,
    CASE
        WHEN extract(DAY FROM MAX(s.created_stamp) - MIN(s.created_stamp)) + 1 = 0 THEN 0
        ELSE COUNT(*) / (EXTRACT(DAY FROM MAX(s.created_stamp) - MIN(s.created_stamp)) + 1)
        END AS average_submission_per_day

FROM
    contest_submission_new s
        LEFT JOIN
    first_attempt fa ON s.user_submission_id = fa.user_submission_id
        LEFT JOIN
    first_accept fat ON s.user_submission_id = fat.user_submission_id
GROUP BY
    s.user_submission_id, fa.first_attempt_score_gt_zero,average_min_rank



