CREATE TABLE student_learning_statistic
(
    login_id                               VARCHAR(255) PRIMARY KEY,
    total_quiz_doing_times                 bigint,
    total_code_submissions                 bigint,
    latest_time_doing_quiz                 timestamp,
    latest_time_submitting_code            timestamp,
    submissions_accepted_on_the_first_time bigint,
    total_quiz_doing_periods               bigint,
    total_error_submissions                bigint,
    created_at                             timestamp,
    last_modified_at                       timestamp,
    CONSTRAINT fk_user_login_id foreign key (login_id) REFERENCES user_login (user_login_id)
);
