create table backlog_project
(
    backlog_project_id   uuid not null default uuid_generate_v1(),
    backlog_project_code varchar(60),
    backlog_project_name varchar(200),
    last_updated_stamp   timestamp     DEFAULT CURRENT_TIMESTAMP,
    created_stamp        timestamp     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_backlog_project primary key (backlog_project_id)
);

create table backlog_task_category
(
    backlog_task_category_id   varchar(60),
    backlog_task_category_name varchar(200),
    last_updated_stamp         timestamp,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_backlog_task_category primary key (backlog_task_category_id)
);

create table backlog_task
(
    backlog_task_id          uuid not null default uuid_generate_v1(),
    backlog_task_name        varchar(200),
    backlog_task_category_id varchar(60),
    backlog_description      text,
    backlog_project_id       uuid not null,
    created_date             timestamp,
    created_by_user_login_id varchar(60),
    from_date                timestamp,
    due_date                 timestamp,
    status_id                varchar(60),
    priority_id              varchar(60),
    attachment_paths         text,
    last_updated_stamp       timestamp     DEFAULT CURRENT_TIMESTAMP,
    created_stamp            timestamp     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_backlog_task primary key (backlog_task_id),
    constraint fk_backlog_task_category_id foreign key (backlog_task_category_id) references backlog_task_category (backlog_task_category_id),
    constraint fk_backlog_task_priority_id foreign key (priority_id) references backlog_task_priority (backlog_task_priority_id),
    constraint fk_backlog_task_status_id foreign key (status_id) references status_item (status_id),
    constraint fk_backlog_task_created_user_login_id foreign key (created_by_user_login_id) references user_login (user_login_id),
    constraint fk_backlog_task_backlog_project_id foreign key (backlog_project_id) references backlog_project (backlog_project_id)
);

create table backlog_task_assignment
(
    backlog_task_assignment_id uuid not null default uuid_generate_v1(),
    backlog_task_id            uuid not null,
    assigned_to_party_id       uuid not null,
    assigned_to_user_login_id  varchar(60),
    start_date                 timestamp,
    finished_date              timestamp,
    status_id                  varchar(60),
    last_updated_stamp         timestamp     DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_backlog_task_assignment primary key (backlog_task_assignment_id),
    constraint fk_backlog_task_assignment_to_assigned_party_id foreign key (assigned_to_party_id) references party (party_id),
    constraint fk_backlog_task_assignment_backlog_task_id foreign key (backlog_task_id) references backlog_task (backlog_task_id),
    constraint fk_backlog_task_assignment_status_id foreign key (status_id) references status_item (status_id)
);

create table backlog_project_member
(
    backlog_project_id uuid not null,
    member_party_id    uuid not null,
    created_stamp      timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_backlog_project_member_id primary key (backlog_project_id, member_party_id),
    constraint fk_backlog_project_id foreign key (backlog_project_id) references backlog_project (backlog_project_id),
    constraint fk_backlog_member_party_id foreign key (member_party_id) references party (party_id)
);

create table backlog_task_priority
(
    backlog_task_priority_id   varchar(60),
    backlog_task_priority_name varchar(200),
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_backlog_task_priority primary key (backlog_task_priority_id)
);

create table backlog_task_assignable
(
    backlog_task_assignable_id uuid not null default uuid_generate_v1(),
    backlog_task_id            uuid not null,
    assigned_to_party_id       uuid not null,
    start_date                 timestamp,
    finished_date              timestamp,
    status_id                  varchar(60),
    last_updated_stamp         timestamp     DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_backlog_task_assignable primary key (backlog_task_assignable_id),
    constraint fk_backlog_task_assignable_to_assigned_party_id foreign key (assigned_to_party_id) references party (party_id),
    constraint fk_backlog_task_assignable_backlog_task_id foreign key (backlog_task_id) references backlog_task (backlog_task_id),
    constraint fk_backlog_task_assignable_status_id foreign key (status_id) references status_item (status_id)
);

CREATE TABLE backlog_task_execution
(
    task_execution_id        uuid NOT NULL,
    task_id                  uuid,
    created_by_user_login_id varchar(60),
    execution_tags           varchar(200),
    comment_id               uuid,
    comment                  text,
    created_stamp            timestamp DEFAULT CURRENT_TIMESTAMP,
    project_id               uuid NOT NULL,
    execution_task_name      varchar(60),
    execution_category       varchar(100),
    execution_status         varchar(200),
    execution_assignee       varchar(200),
    execution_priority       varchar(200),
    execution_due_date       varchar(200),
    execution_file_name      varchar(200),
    CONSTRAINT pk_backlog_task_execution_id PRIMARY KEY (task_execution_id),
    CONSTRAINT fk_backlog_task_execution_created_by_user_login_id FOREIGN KEY (created_by_user_login_id) REFERENCES user_login (user_login_id),
    CONSTRAINT fk_backlog_task_execution_task_id FOREIGN KEY (task_id) REFERENCES backlog_task (backlog_task_id)
);

CREATE TABLE backlog_skill
(
    backlog_skill_id  varchar(60),
    skill_name        varchar(200),
    skill_description text,
    CONSTRAINT pk_backlog_skill PRIMARY KEY (backlog_skill_id)
)

create table backlog_task_skill
(
    backlog_task_skill_id uuid        not null default uuid_generate_v1(),
    backlog_task_id       uuid        not null,
    backlog_skill_id      varchar(60) not null,
    constraint pk_backlog_task_skill primary key (backlog_task_skill_id),
    constraint fk_backlog_task_skill_task_id foreign key (backlog_task_id) references backlog_task (backlog_task_id),
    constraint fk_backlog_task_skill_skill_id foreign key (backlog_skill_id) references backlog_skill (backlog_skill_id)
)

create table backlog_user_login_skill
(
    backlog_user_login_skill_id uuid not null default uuid_generate_v1(),
    user_login_id               varchar(255),
    backlog_skill_id            varchar(60),
    constraint pk_backlog_user_login_skill primary key (backlog_user_login_skill_id),
    constraint fk_backlog_user_login_skill_user_login_id foreign key (user_login_id) references user_login (user_login_id),
    constraint fk_backlog_user_login_skill_id foreign key (backlog_skill_id) references backlog_skill (backlog_skill_id)
)

create table backlog_task_comment
(
    backlog_task_comment_id  uuid not null,
    comment                  text,
    task_id                  uuid,
    status                   boolean,
    created_by_user_login_id varchar(60),
    created_stamp            timestamp DEFAULT CURRENT_TIMESTAMP,
    last_updated_stamp       timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_backlog_task_comment primary key (backlog_task_comment_id),
    constraint fk_backlog_task_comment_task_id foreign key (task_id) references backlog_task (backlog_task_id)
)

alter table backlog_task_execution
    add comment_id UUID

alter table backlog_task_execution
    add constraint fk_backlog_task_execution_comment_id foreign key (comment_id) references backlog_task_comment (backlog_task_comment_id)
