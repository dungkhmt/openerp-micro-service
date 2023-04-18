create table department
(
    department_id           VARCHAR(60),
    department_name         VARCHAR(100),
    party_id                UUID,
    start_date              TIMESTAMP,
    created_by_userLogin_id VARCHAR(255),
    last_updated_stamp      TIMESTAMP,
    created_stamp           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_department primary key (department_id),
    constraint fk_department_create_by_user_login_id foreign key (created_by_userLogin_id) references user_login (user_login_id)
);

create table party_department
(
    party_department_id uuid not null default uuid_generate_v1(),
    party_id            uuid not null,
    department_id       VARCHAR(60),
    role_type_id        VARCHAR(60),
    from_date           TIMESTAMP,
    thru_date           TIMESTAMP,

    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_party_department primary key (party_department_id),
    constraint fk_party_department_role_type_id foreign key (role_type_id) references role_type (role_type_id),
    constraint fk_party_department_party_id foreign key (party_id) references party (party_id),
    constraint fk_party_department_department_id foreign key (department_id) references department (department_id)
);
