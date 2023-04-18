
create table quiz_course_topic(
    quiz_course_topic_id varchar(50),
    quiz_course_topic_name varchar(200),
    course_id varchar(10),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_quiz_course_topic primary key(quiz_course_topic_id),
    constraint fk_quiz_course_topic_course_id foreign key(course_id) references edu_course(id)
);

create table quiz_question(
    question_id uuid not null default uuid_generate_v1(),
    course_topic_id varchar(60),
    level_id varchar(50),
    question_name varchar(500),
    question_content text,
    attachment varchar(500),
    status_id varchar(30),
    solution_content text,
    solution_attachment varchar(500),
    created_by_user_login_id varchar(60),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_quiz_question_id primary key(question_id),
    constraint fk_quiz_question_created_by_user_login_id foreign key(created_by_user_login_id) references user_login(user_login_id),
    constraint fk_quiz_question_course_id foreign key(course_topic_id) references quiz_course_topic(quiz_course_topic_id)
);

create table quiz_choice_answer(
    choice_answer_id uuid not null default uuid_generate_v1(),
    choice_answer_code varchar(20),
    choice_answer_content text,
    question_id uuid,
    is_correct_answer char,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_quiz_choice_answer primary key(choice_answer_id),
    constraint fk_quiz_choice_answer_question_id foreign key(question_id) references quiz_question(question_id),

);

create table quiz_question_user_role(
    id uuid not null default uuid_generate_v1(),
    question_id uuid,
    user_id varchar(60),
    role_id varchar(100),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_quiz_question_user_role primary key(id),
    constraint fk_quiz_question_user_role_question_id foreign key(question_id) references quiz_question(question_id),
    constraint fk_quiz_question_user_role_user_id foreign key(user_id) references user_login(user_login_id)
);

create table log_user_login_course_chapter_material(
    user_login_course_chapter_material_id uuid not null default uuid_generate_v1(),
    user_login_id varchar(60),
    edu_course_material_id uuid,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_user_login_course_chapter_material_id primary key(user_login_course_chapter_material_id),
    constraint fk_user_login_course_chapter_material_userlogin foreign key(user_login_id) references user_login(user_login_id),
    constraint fk_user_login_course_chapter_material_course_chapter_material foreign key(edu_course_material_id) references edu_course_chapter_material(edu_course_material_id)
);

alter table log_user_login_course_chapter_material ADD COLUMN edu_class_id uuid references edu_class (id);

create table log_user_login_quiz_question(
    log_user_login_quiz_question_id uuid not null default uuid_generate_v1(),
    user_login_id varchar(60),
    question_id uuid,
    question_topic_id varchar(60),
    question_topic_name varchar(200),
    is_correct_answer char,
    class_code varchar(60),
    class_id uuid,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_log_user_login_quiz_question_id primary key(log_user_login_quiz_question_id),
    constraint fk_log_user_login_quiz_question_userlogin foreign key(user_login_id) references user_login(user_login_id),
    constraint fk_log_user_login_quiz_question_question_id foreign key(question_id) references quiz_question(question_id)
);

create table edu_quiz_test(
    test_id varchar(60),
    test_name varchar(200),
    schedule_datetime timestamp,
    duration int,
    course_id varchar(10),
    class_id uuid,
    session_id uuid,
    participant_quiz_group_assignment_mode varchar(100),
    judge_mode varchar(200),
    view_type_id varchar(100),
    question_statement_view_type_id varchar(200),
    status_id varchar(30),
    created_by_user_login_id varchar(60),

    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint pk_edu_quiz_test_id primary key(test_id),
    constraint fk_edu_quiz_test_created_by_user_login_id foreign key(created_by_user_login_id) references user_login(user_login_id),
    constraint fk_edu_quiz_test_course_id foreign key(course_id) references edu_course(id),
    constraint fk_edu_quiz_test_session foreign key(session_id) references edu_class_session(session_id),
    constraint fk_edu_quiz_test_class_id foreign key(class_id) references edu_class(id)
);

create table edu_test_quiz_group(
    quiz_group_id uuid not null default uuid_generate_v1(),
    test_id varchar(60),
    group_code varchar(60),
    note varchar(500),
	status_id varchar(60),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_edu_test_quiz_group primary key(quiz_group_id),
    constraint fk_edu_test_quiz_group foreign key(test_id) references edu_quiz_test(test_id)
);

create table edu_test_quiz_group_participation_assignment(
    quiz_group_id uuid,
    participation_user_login_id varchar(60),
    status_id varchar(50),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_edu_test_quiz_group_participation_assignment primary key(quiz_group_id,participation_user_login_id),
    constraint fk_edu_test_quiz_group_participation_assignment_user_login_id foreign key(participation_user_login_id) references user_login(user_login_id)
);

create table quiz_group_question_assignment(
    question_id uuid,
    quiz_group_id uuid,
    status_id varchar(50),
    seq int,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_quiz_group_question_assignment primary key(quiz_group_id, question_id),
    constraint fk_quiz_group_question_assignment foreign key(question_id) references quiz_question(question_id)
);

create table quiz_group_question_participation_execution(
    question_id uuid,
    quiz_group_id uuid,
    participation_user_login_id varchar(60),
    grade int,
    result char,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_quiz_group_question_participation_execution primary key(participation_user_login_id, quiz_group_id, question_id),
    constraint fk_quiz_group_question_participation_execution_user_login foreign key(participation_user_login_id) references user_login(user_login_id),
    constraint fk_quiz_group_question_participation_execution_quiz_group foreign key(quiz_group_id) references edu_test_quiz_group(quiz_group_id),
    constraint fk_quiz_group_question_participation_execution_question foreign key(question_id) references quiz_question(question_id)
);

create table quiz_group_question_participation_execution_choice(
    question_id uuid,
    quiz_group_id uuid,
    participation_user_login_id varchar(60),
    choice_answer_id uuid,
    submission_id uuid,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint pk_quiz_group_question_participation_execution_choice primary key(participation_user_login_id, quiz_group_id, question_id, choice_answer_id),
    constraint fk_quiz_group_question_participation_execution_choice_user_login foreign key(participation_user_login_id) references user_login(user_login_id),
    constraint fk_quiz_group_question_participation_execution_choice_quiz_group foreign key(quiz_group_id) references edu_test_quiz_group(quiz_group_id),
    constraint fk_quiz_group_question_participation_execution_choice_question foreign key(question_id) references quiz_question(question_id),
    constraint fk_quiz_group_question_participation_execution_choice_answer foreign key(choice_answer_id) references quiz_choice_answer(choice_answer_id)
);

create table quiz_test_execution_submission(
    submission_id uuid not null default uuid_generate_v1(),
    question_id uuid,
    quiz_group_id uuid,
    participation_user_login_id varchar(60),
    choice_answer_ids varchar(2000),
    status_id varchar(200),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint pk_quiz_test_execution_submission primary key(submission_id),
    constraint fk_quiz_test_execution_submission_user_login foreign key(participation_user_login_id) references user_login(user_login_id),
    constraint fk_quiz_test_execution_submission_quiz_group foreign key(quiz_group_id) references edu_test_quiz_group(quiz_group_id),
    constraint fk_quiz_test_execution_submission_question foreign key(question_id) references quiz_question(question_id)

);


create table history_log_quiz_group_question_participation_execution_choice(
    log_id uuid not null default uuid_generate_v1(),
    question_id uuid,
    quiz_group_id uuid,
    participation_user_login_id varchar(60),
    choice_answer_id uuid,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_his_log_quiz_group_question_participation_execution_choice primary key(log_id)

);

create table edu_test_quiz_participant(
    test_id varchar(60),
    participant_user_login_id varchar(60),
    status_id varchar(60),
    permutation varchar(50),
    role_id varchar(100),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint pk_edu_test_participation primary key(test_id, participant_user_login_id),
    constraint fk_edu_test_participation_test_id foreign key(test_id) references edu_quiz_test(test_id),
    constraint fk_edu_test_participation_participation_user_login_id foreign key(participant_user_login_id)
                                            references user_login(user_login_id)


);
create table edu_test_quiz_role(
    test_id varchar(60),
    participant_user_login_id varchar(60),
    status_id varchar(60),
    permutation varchar(50),
    role_id varchar(100),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint pk_edu_test_role primary key(test_id, participant_user_login_id,role_id),
    constraint fk_edu_test_role_test_id foreign key(test_id) references edu_quiz_test(test_id),
    constraint fk_edu_test_role_participation_user_login_id foreign key(participant_user_login_id)
                                            references user_login(user_login_id)


);

create table edu_quiz_test_quiz_question(
    test_id varchar(60) not null,
    question_id uuid not null,
    created_by_user_login_id varchar(60),
    status_id varchar(60),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_edu_quiz_test_quiz_question primary key(test_id,question_id),
    constraint fk_edu_quiz_test_quiz_question_test_id foreign key(test_id) references edu_quiz_test(test_id),
    constraint fk_edu_quiz_test_quiz_question_question_id foreign key(question_id) references quiz_question(question_id),
    constraint fk_edu_quiz_test_quiz_question_created_by_user_login_id foreign key(created_by_user_login_id) references user_login(user_login_id)
);

create table comment_on_quiz_question(
    comment_id uuid not null default uuid_generate_v1(),
    question_id uuid,
    comment_text text,
    reply_to_comment_id uuid,
    created_by_user_login_id varchar(60),
    status_id varchar(60),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_comment_on_quiz_question primary key(comment_id),
    constraint fk_comment_on_quiz_question_created_by_user_login_id foreign key(created_by_user_login_id) references user_login(user_login_id),
    constraint fk_comment_on_quiz_question_question_id foreign key(question_id) references quiz_question(question_id),
    constraint fk_comment_on_quiz_question_reply_to_comment_id foreign key(reply_to_comment_id) references  comment_on_quiz_question(comment_id)
);

create table solution_hint_to_quiz_question(
    solution_id uuid not null default uuid_generate_v1(),
    question_id uuid,
    solution_text text,
    created_by_user_login_id varchar(60),
    status_id varchar(60),
    attachment varchar(500),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint pk_solution_hint_to_quiz_question primary key (solution_id),
    constraint fk_solution_hint_to_quiz_question_question_id foreign key(question_id) references quiz_question(question_id),
    constraint fk_solution_hint_to_quiz_question_created_by_user_login_id foreign key(created_by_user_login_id) references user_login(user_login_id)
);

create table quiz_question_course_topic(
  question_id uuid not null,
  quiz_course_topic_id varchar(50),
  from_date timestamp,
  thru_date timestamp,
    created_by_user_login_id varchar(60),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);

create table participant_doing_quiz_add_explanation(
    id uuid not null default uuid_generate_v1(),
    question_id uuid not null,
    participant_user_id varchar(60),
    test_id varchar(60),
    solution_explanation text,
    attachment varchar(500),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_participant_doing_quiz_add_explanation primary key (id),
    constraint fk_participant_doing_quiz_add_explanation_question foreign key(question_id) references quiz_question(question_id),
    constraint fk_participant_doing_quiz_add_explanation_participant foreign key(participant_user_id) references user_login(user_login_id)
);

