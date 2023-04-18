create table contest_problem_new
(
    problem_id varchar(100) not null,
    problem_name varchar(100) unique ,
    problem_description text, -- problem_statement
    created_by_user_login_id varchar(60),
    time_limit  int,
    memory_limit int,
    level_id varchar(60),
    category_id varchar(60),
    last_updated_stamp         timestamp DEFAULT current_date ,
    created_stamp              timestamp DEFAULT current_date ,
    solution text,
    level_order int,
    correct_solution_source_code text,
    correct_solution_language varchar(10),
    solution_checker_source_code text,
    solution_checker_source_language varchar(10),
    is_public bool,
    constraint pk_contest_problem_new primary key (problem_id),
    constraint fk_contest_problem_new foreign key (created_by_user_login_id) references user_login(user_login_id)
);

create table user_contest_problem_role(
    id uuid not null default uuid_generate_v1(),
    user_id varchar(60),
    problem_id varchar(100),
    role_id varchar(100),
    update_by_user_id varchar(60),
    last_updated_stamp         timestamp DEFAULT current_date ,
    created_stamp              timestamp DEFAULT current_date ,
    constraint pk_user_contest_problem_role primary key(id),
    constraint fk_user_contest_problem_role_updated_by_user_id foreign key(update_by_user_id) references user_login(user_login_id),
    constraint fk_user_contest_problem_role_user_id foreign key(user_id) references user_login(user_login_id),
    constraint fk_user_contest_problem_role_problem_id foreign key(problem_id) references contest_problem_new(problem_id)
);

create table problem_source_code_new
(
    problem_source_code_id varchar (70),
    base_source text,
    main_source text,
    problem_function_default_source text,
    problem_function_solution text,
    language varchar (10),
    contest_problem_id varchar(60),
    last_updated_stamp         timestamp DEFAULT current_date ,
    created_stamp              timestamp DEFAULT current_date ,
    constraint pk_source_code_new primary key(problem_source_code_id),
    constraint fk_contest_problem_new foreign key (contest_problem_id) references contest_problem_new(problem_id)
);


create table test_case_new
(
    test_case_id  UUID NOT NULL default uuid_generate_v1(),
    test_case_point int,
    test_case text,
    correct_answer text,
    contest_problem_id varchar(60),
    is_public varchar(1),
    status_id varchar(100),
    description text,
    last_updated_stamp         timestamp DEFAULT current_date ,
    created_stamp              timestamp DEFAULT current_date ,
    constraint pk_contest_problem_test_case_new primary key (test_case_id),
    constraint fk_contest_problem_test_case_problem_id_new foreign key (contest_problem_id) references contest_problem_new(problem_id)
);

create table problem_submission_new
(
    problem_submission_id UUID NOT NULL default uuid_generate_v1(),
    problem_id  varchar(100) not null,
    submitted_by_user_login_id varchar(60),
    source_code text,
    source_code_language varchar (10),
    status varchar(20),
    disable_status varchar(1),
    score int,
    runtime varchar(10),
    memory_usage float ,
    test_case_pass varchar (10),
    created_stamp              timestamp (25),
    constraint fk_problem_submission_id_new primary key(problem_submission_id),
    constraint fk_problem_id_new foreign key (problem_id) references contest_problem_new(problem_id),
    constraint fk_user_login_id_new foreign key (submitted_by_user_login_id) references user_login(user_login_id)
);

-- drop table problem_submission,  test_case,  contest_problem, problem_source_code;

create table contest_new
(
    contest_id varchar (100) not null ,
    contest_name varchar (100),
    contest_solving_time int,
    user_create_id varchar (60),
    try_again BOOLEAN,
    public BOOLEAN,
    count_down numeric NULL,
    problem_description_view_type varchar(200),
	started_count_down_time timestamp NULL,
	end_time timestamp NULL,
	status_id varchar(100),
	submission_action_type varchar(200),
	max_number_submission int,
	min_time_between_two_submissions int,
	judge_mode varchar(100),
    participant_view_result_mode varchar(200),
    use_cache_contest_problem varchar(1),
    max_source_code_length int,
    evaluate_both_public_private_testcase varchar(1);
    last_updated_stamp         timestamp DEFAULT current_date ,
    created_stamp              timestamp DEFAULT current_date ,
    constraint pk_contest_id_new primary key (contest_id),
    constraint fk_user_create_contest_new foreign key (user_create_id) references user_login(user_login_id)
);

create table contest_contest_problem_new
(
    contest_id varchar (100) not null ,
    problem_id varchar (100) not null ,
    submission_mode varchar(100),
    problem_rename varchar(200),
    problem_recode varchar(20),
    last_updated_stamp         timestamp DEFAULT current_date ,
    created_stamp              timestamp DEFAULT current_date ,
    constraint fk_contest_id_contest_contest_problem_new foreign key (contest_id) references contest_new(contest_id),
    constraint fk_problem_id_contest_contest_problem_new foreign key (problem_id) references contest_problem_new(problem_id)
);

create table contest_submission_new
(
    contest_submission_id  UUID NOT NULL default uuid_generate_v1(),
    contest_id varchar (100) not null ,
    problem_id varchar (100) not null ,
    user_submission_id varchar (100) not null ,
    submitted_by_user_id varchar(100),
--     problem_submission_id UUID,
    status varchar (20),
    management_status varchar(20),
    point int,
    test_case_pass varchar (20),
    source_code text,
    source_code_language varchar (10),
    runtime bigint ,
    memory_usage float ,
    message text,
    last_updated_by_user_id varchar(60),
    last_updated_stamp         timestamp default current_date ,
    created_stamp              timestamp default current_date ,
    constraint pk_contest_submission_id_contest_submission_new primary key (contest_submission_id),
    constraint fk_contest_id_contest_submission_new foreign key (contest_id) references contest_new(contest_id),
    constraint fk_problem_id_contest_submission_new foreign key (problem_id) references contest_problem_new(problem_id),
    constraint fk_user_submission_id_contest_submission_new foreign key (user_submission_id) references user_login(user_login_id)
--     constraint fk_problem_submission_id_contest_submission_new foreign key(problem_submission_id) references problem_submission_new(problem_submission_id)
);
create table contest_submission_history(
    contest_submission_history_id uuid NOT NULL default uuid_generate_v1(),
    contest_submission_id uuid NOT NULL,
    modified_source_code_submitted text,
    language varchar(10),
    contest_id varchar(100),
    problem_id varchar(100),
    last_updated_stamp         timestamp default current_date ,
    created_stamp              timestamp default current_date ,
    constraint pk_contest_submission_history_id primary key (contest_submission_history_id),
    constraint fk_contest_submission_history_problem_id foreign key(problem_id) references contest_problem_new(problem_id),
    constraint fk_contest_submission_history_contest_id foreign key(contest_id) references contest_new(contest_id),
    constraint fk_contest_submission_history_submission_id foreign key (contest_submission_id) references  contest_submission_new(contest_submission_id)

);
create table contest_submission_testcase_new(
    contest_submission_testcase_id uuid not null default uuid_generate_v1(),
    contest_submission_id uuid,
    contest_id varchar(100),
    problem_id varchar(100),
    submitted_by_user_login_id varchar(100),
    test_case_id uuid,
    point int,
    status text,
    runtime int,
    memory_usage int,
    participant_solution_output text,
    test_case_output text,
    last_updated_stamp         timestamp default current_date ,
    created_stamp              timestamp default current_date ,
    constraint pk_contest_submission_testcase_new primary key(contest_submission_testcase_id),
    constraint fk_contest_submission_testcase_new_submission_id foreign key(contest_submission_id) references contest_submission_new(contest_submission_id),
    constraint fk_contest_submission_testcase_new_contest foreign key  (contest_id) references contest_new(contest_id),
    constraint fk_contest_submission_testcase_new_problem foreign key (problem_id) references contest_problem_new(problem_id),
    constraint fk_contest_submission_testcase_new_userlogin foreign key (submitted_by_user_login_id) references user_login(user_login_id),
    constraint fk_contest_submission_testcase_new_testcase foreign key (test_case_id) references test_case_new(test_case_id)
);

create table user_submission_contest_result_new
(
--     user_submission_contest_result_id UUID NOT NULL default uuid_generate_v1(),
    contest_id varchar (100) not null ,
    user_id varchar (100) not null,
    point int not null,
    email varchar(20),
    full_name varchar(50),
    last_updated_stamp         timestamp default current_date ,
    created_stamp              timestamp default current_date ,
    constraint pk_user_submission_result_id_user_submission_result_new primary key (contest_id, user_id),
    constraint fk_contest_id_user_submission_result_new foreign key (contest_id) references contest_new(contest_id),
    constraint fk_user_id_user_submission_result_new foreign key (user_id) references user_login(user_login_id)
);

create table user_registration_contest_new
(
    user_registration_contest_id UUID NOT NULL default uuid_generate_v1(),
    user_id varchar (100) not null ,
    contest_id varchar (100) not null ,
    status varchar (20) not null,
    role_id varchar(100),
    created_date timestamp,
    last_updated timestamp,
    updated_by_user_login_id varchar(60),
    permission_id varchar(200),
    constraint fk_user_id_user_registration_contest_new foreign key (user_id) references user_login(user_login_id),
    constraint fk_contest_id_user_registration_contest_new foreign key (contest_id) references contest_new(contest_id)
);

create table contest_role(
    contest_id varchar(100),
    user_login_id varchar(60),
    role_id varchar(100),
    from_date timestamp,
    thru_date timestamp,
    last_updated_stamp         timestamp default current_date ,
    created_stamp              timestamp default current_date ,
    constraint pk_contest_role primary key (contest_id, user_login_id, role_id, from_date),
    constraint fk_contest_role_contest_id foreign key(contest_id) references contest_new(contest_id),
    constraint fk_contest_role_user_login_id foreign key(user_login_id) references user_login(user_login_id)

);

create table code_plagiarism(
    plagiarism_id uuid not null default uuid_generate_v1(),
    contest_id varchar(100) not null,
    problem_id varchar(100) not null,
    user_id_1 varchar(60),
    user_fullname1 varchar(200),
    user_id_2 varchar(60),
    user_fullname2 varchar(200),
    source_code_1 text,
    source_code_2 text,
    submit_date1 timestamp,
    submit_date2 timestamp,
    score numeric,

    last_updated_stamp         timestamp default current_date ,
    created_stamp              timestamp default current_date ,

    constraint pk_code_plagiarism_id primary key(plagiarism_id),
    constraint fk_code_plagiarism_user_1 foreign key(user_id_1) references user_login(user_login_id),
    constraint fk_code_plagiarism_user_2 foreign key(user_id_2) references user_login(user_login_id),
    constraint fk_code_plagiarism_contest foreign key(contest_id) references contest_new(contest_id),
    constraint fk_code_plagiarism_problem foreign key(problem_id) references contest_problem_new(problem_id)
);

create table tag
(
    tag_id varchar (100) not null ,
    name varchar (100),
    description text,
    constraint pk_tag_id primary key(tag_id)
);

create table problem_tag
(
    tag_id varchar (100) not null ,
    problem_id varchar (100) not null ,
    constraint fk_tag_id_tag_contest_problem_new foreign key(tag_id) references tag(tag_id),
    constraint fk_problem_id_tag_contest_problem_new foreign key(problem_id) references contest_problem_new(problem_id)
);



