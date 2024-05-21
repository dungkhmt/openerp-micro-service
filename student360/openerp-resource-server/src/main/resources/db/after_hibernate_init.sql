CREATE OR REPLACE VIEW public.max_point_contest_view
AS SELECT ccpn.contest_id,
          sum(tsn.test_case_point) AS total_point
   FROM test_case_new tsn
            JOIN contest_contest_problem_new ccpn ON tsn.contest_problem_id::text = ccpn.problem_id::text
   WHERE upper(ccpn.contest_id::text) ~~ upper('%midterm%'::text) OR upper(ccpn.contest_id::text) ~~ upper('%final%'::text)
   GROUP BY ccpn.contest_id
   ORDER BY ccpn.contest_id;


CREATE OR REPLACE VIEW public.midterm_final_submission_view
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
   WHERE ranked.rn = 1;

CREATE OR REPLACE VIEW submission_hourly_summary AS
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
