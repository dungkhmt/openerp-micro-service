
create table contest_problem
(
    problem_id varchar(60) not null,
    problem_name varchar(200),
    problem_statement text,
    created_by_user_login_id varchar(60),
    time_limit  int,
    memory_limit int,
    level_id varchar(60),
    category_id varchar(60),
    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_contest_problem primary key (problem_id),
    constraint fk_contest_problem foreign key (created_by_user_login_id) references user_login(user_login_id)
);

create table contest_problem_test(
    problem_test_id uuid not null default uuid_generate_v1(),
    problem_id varchar(60),
    problem_test_filename varchar(200),
    problem_test_point int,

    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_contest_problem_test primary key (problem_test_id),
    constraint fk_contest_problem_test_problem_id foreign key (problem_id) references contest_problem(problem_id)

);

create table programming_contest(
    contest_id varchar(60),
    contest_name varchar(200),
    created_by_user_login_id varchar(60),
    contest_type_id varchar(60),
    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_contest_id primary key (contest_id),
    constraint fk_contest_create_by_user_login_id foreign key (created_by_user_login_id) references user_login(user_login_id)
);
create table programming_contest_problem(
    contest_id varchar(60),
    problem_id varchar(60),
    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_programming_contest_problem primary key(contest_id,problem_id),
    constraint fk_programming_contest_problem_contest_id foreign key(contest_id) references programming_contest(contest_id),
    constraint fk_programming_contest_problem_problem_id foreign key(problem_id) references contest_problem(problem_id)
);

create table programming_contest_user_registration(
    contest_id varchar(60),
    user_login_id varchar(60),
    status_id varchar(60),
    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_programming_contest_user_registration primary key(contest_id, user_login_id),
    constraint fk_programming_contest_user_registration_contest_id foreign key(contest_id) references programming_contest(contest_id),
    constraint fk_programming_contest_user_registration_user_login_id foreign key(user_login_id) references user_login(user_login_id)

);
create table programming_contest_user_registration_problem(
    contest_id varchar(60),
    user_login_id varchar(60),
    problem_id varchar(60),
    points int,
    last_points int,

    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_programming_contest_user_registration_problem primary key(contest_id, user_login_id,problem_id),
    constraint fk_programming_contest_user_registration_problem_contest_id foreign key(contest_id) references programming_contest(contest_id),
    constraint fk_programming_contest_user_registration_problem_user_login_id foreign key(user_login_id) references user_login(user_login_id),
    constraint fk_programming_contest_user_registration_problem_problem_id foreign key(problem_id) references contest_problem(problem_id)

);

create table contest_program_submission(
    contest_program_submission_id uuid not null default uuid_generate_v1(),
    contest_id varchar(60),
    problem_id varchar(60),
    submitted_by_user_login_id varchar(60),
    points int,
    full_link_file varchar(1024),
    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_contest_program_submission primary key (contest_program_submission_id),
    constraint fk_contest_program_submission_contest_id foreign key(contest_id) references programming_contest(contest_id),
    constraint fk_contest_program_submission_problem_id foreign key(problem_id) references contest_problem(problem_id),
    constraint fk_contest_program_submission_submitted_by_user_login_id foreign key(submitted_by_user_login_id) references user_login(user_login_id)
);
