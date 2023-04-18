create table training_program(
    id uuid not null default uuid_generate_v1(),
    name varchar(500),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_training_program_id primary key(id)
);

create table thesis_defense_plan(
    id varchar(60),
    name varchar(500),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_thesis_defense_plan primary key(id)

);

create table defense_jury(
    id uuid not null default uuid_generate_v1(),
    name varchar(200),
    program_id varchar(200),
    thesis_defense_plan_id varchar(60),
    defense_date timestamp,
    created_by_user_login_id varchar(60),
    max_nbr_thesis int,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_defense_jury primary key(id),
    constraint fk_defense_jury_plan foreign key(thesis_defense_plan_id) references thesis_defense_plan(id),
    constraint fk_defense_jury_created_by_user_login_id foreign key(created_by_user_login_id) references user_login(user_login_id)

);

create table thesis(
    id uuid not null default uuid_generate_v1(),
    thesis_name varchar(500),
    thesis_abstract text,
    program_id uuid,
    thesis_defense_plan_id varchar(60),
    student_name varchar(200),
    supervisor_id varchar(60),
    created_by_user_login_id varchar(60),
    scheduled_jury_id uuid,
    scheduled_reviewer_id varchar(60),
    keywords text,

    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_thesis_id primary key(id),
    constraint fk_thesis_program foreign key(program_id) references training_program(id),
    constraint fk_thesis_thesis_defense_plan_id foreign key(thesis_defense_plan_id) references thesis_defense_plan(id),
    constraint fk_thesis_created_by_user_login_id foreign key(created_by_user_login_id) references user_login(user_login_id),
    constraint fk_thesis_supervisor_id foreign key(supervisor_id) references teacher(id),
    constraint fk_thesis_scheduled_reviewer_id foreign key(scheduled_reviewer_id) references teacher(id),
    constraint fk_thesis_scheduled_jury foreign key(scheduled_jury_id) references defense_jury(id)
);

create table academic_keyword(
    keyword varchar(256),
    description text,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_academic_keyword primary key(keyword)

);

create table thesis_keyword(
    thesis_id uuid,
    keyword varchar(256),
    constraint pk_thesis_keyword primary key(thesis_id, keyword),
    constraint fk_thesis_keyword_thesis foreign key(thesis_id) references thesis(id),
    constraint fk_thesis_keyword_keyword foreign key (keyword) references academic_keyword(keyword)
);

create table teacher_thesis_defense_plan(
    teacher_id varchar(60),
    thesis_defense_plan_id varchar(60),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_teacher_thesis_defense_plan primary key(teacher_id,thesis_defense_plan_id),
    constraint fk_teacher_thesis_defense_plan_teacher_id foreign key(teacher_id) references teacher(id),
    constraint fk_teacher_thesis_defense_planplan_id foreign key(thesis_defense_plan_id) references thesis_defense_plan(id)
);

create table teacher_keyword(
    teacher_id varchar(60),
    keyword varchar(100),

    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_teacher_keyword primary key(teacher_id,keyword),
    constraint fk_teacher_keyword_teacher_id foreign key(teacher_id) references teacher(id),
    constraint fk_teacher_keyword_keyword foreign key(keyword) references academic_keyword(keyword)
);


create table defense_jury_teacher(
    teacher_id varchar(60),
    jury_id uuid,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_defense_jury_teacher primary key(teacher_id,jury_id),
    constraint fk_defense_jury_teacher_id foreign key(teacher_id) references teacher(id),
    constraint fk_defense_jury_jury foreign key(jury_id) references defense_jury(id)
);
