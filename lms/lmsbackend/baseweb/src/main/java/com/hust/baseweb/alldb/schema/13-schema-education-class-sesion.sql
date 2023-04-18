create table edu_class_session(
    session_id uuid not null default uuid_generate_v1(),
    session_name varchar(200),
    class_id uuid,
    start_datetime timestamp,
    created_by_user_login_id varchar(60),
    status_id varchar(200),
    description text,

    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_edu_class_session primary key(session_id),
    constraint fk_edu_class_session_created_by_user_login foreign key(created_by_user_login_id) references user_login(user_login_id),
    constraint fk_edu_class_session_class_id foreign key(class_id) references edu_class(id)

);

create table edu_class_session_snapshot_quiz(
    session_id uuid,
    question_id uuid,
    status_id varchar(100),
    start_datetime timestamp,

    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_edu_class_session_snapshot_quiz(session_id, question_id),
    constraint fk_edu_class_session_snapshot_quiz_session foreign key(session_id) references edu_class_session(session_id),
    constraint fk_edu_class_session_snapshot_quiz_question foreign key(question_id) references quiz_question(question_id)

);

create table edu_class_session_snapshot_quiz_participant_choice(
    session_id uuid,
    question_id uuid,
    participation_user_login_id varchar(60),
    choice_answer_id uuid,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    constraint pk_edu_class_session_snapshot_quiz_participant_choice primary key(participation_user_login_id, session_id, question_id, choice_answer_id),
    constraint fk_edu_class_session_snapshot_quiz_participant_choice_user_login foreign key(participation_user_login_id) references user_login(user_login_id),
    constraint fk_edu_class_session_snapshot_quiz_participant_choice_session foreign key(session_id) references edu_class_session(session_id),
    constraint fk_edu_class_session_snapshot_quiz_participant_choice_question foreign key(question_id) references quiz_question(question_id),
    constraint fk_edu_class_session_snapshot_quiz_participant_choice_answer foreign key(choice_answer_id) references quiz_choice_answer(choice_answer_id)

);
