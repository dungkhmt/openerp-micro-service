-- Minimal tables to login and start develop new module
SELECT *
FROM pg_catalog.pg_tables
WHERE schemaname = 'public';

-- For login feature
CREATE TABLE status_type
(
    status_type_id     VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_status_type PRIMARY KEY (status_type_id),
    CONSTRAINT status_type_parent FOREIGN KEY (parent_type_id) REFERENCES status_type (status_type_id)
);

CREATE TABLE status
(
    status_id          VARCHAR(60) NOT NULL,
    status_type_id     VARCHAR(60),
    status_code        VARCHAR(60),
    sequence_id        VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_status PRIMARY KEY (status_id),
    CONSTRAINT status_to_type FOREIGN KEY (status_type_id) REFERENCES status_type (status_type_id)
);

CREATE TABLE party_type
(
    party_type_id      VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    has_table          BOOLEAN,
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_party_type PRIMARY KEY (party_type_id),
    CONSTRAINT party_type_par FOREIGN KEY (parent_type_id) REFERENCES party_type (party_type_id)
);

CREATE TABLE party
(
    party_id                    UUID NOT NULL,
    party_type_id               VARCHAR(60),
    external_id                 VARCHAR(60),
    description                 TEXT,
    status_id                   VARCHAR(60),
    created_date                TIMESTAMP NULL,
    created_by_user_login       VARCHAR(255),
    last_modified_date          TIMESTAMP NULL,
    last_modified_by_user_login VARCHAR(255),
    is_unread                   BOOLEAN,
    last_updated_stamp          TIMESTAMP,
    created_stamp               TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    party_code                  VARCHAR(255),
    name                        varchar(255),
    CONSTRAINT pk_party PRIMARY KEY (party_id),
    CONSTRAINT party_status_item FOREIGN KEY (status_id) REFERENCES status (status_id),
    CONSTRAINT party_pty_typ FOREIGN KEY (party_type_id) REFERENCES party_type (party_type_id)
);

CREATE TABLE person
(
    party_id           UUID NOT NULL,
    first_name         VARCHAR(100),
    middle_name        VARCHAR(100),
    last_name          VARCHAR(100),
    gender             CHARACTER(1),
    birth_date         DATE,
    last_updated_stamp TIMESTAMP NULL,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_person PRIMARY KEY (party_id),
    CONSTRAINT person_party FOREIGN KEY (party_id) REFERENCES party (party_id)
);

CREATE TABLE user_login
(
    user_login_id            VARCHAR(255) NOT NULL,
    current_password         VARCHAR(60),
    otp_secret               VARCHAR(60),
    client_token             VARCHAR(512),
    password_hint            TEXT,
    is_system                BOOLEAN,
    enabled                  BOOLEAN,
    has_logged_out           BOOLEAN,
    require_password_change  BOOLEAN,
    disabled_date_time       TIMESTAMP NULL,
    successive_failed_logins INTEGER,
    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    otp_resend_number        INT       DEFAULT 0 NULL,
    party_id                 UUID,
    email                    VARCHAR(100),
    CONSTRAINT pk_user_login PRIMARY KEY (user_login_id),
    CONSTRAINT user_party FOREIGN KEY (party_id) REFERENCES party (party_id)
);

create table security_group
(
    group_id           varchar(255) primary key,
    description        varchar(255),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    group_name         varchar(100)
);

create table user_login_security_group
(
    user_login_id      varchar(255),
    group_id           varchar(255),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key (user_login_id, group_id),
    constraint user_secgrp_grp foreign key (group_id) references security_group (group_id)
);

create table security_permission
(
    permission_id      varchar(100) primary key,
    description        varchar(100),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table security_group_permission
(
    group_id           varchar(60),
    permission_id      varchar(100),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key (permission_id, group_id),
    constraint sec_grp_perm_grp foreign key (group_id) references security_group (group_id),
    constraint sec_grp_perm_perm foreign key (permission_id) references security_permission (permission_id)
);

-- For menu listing
create table application_type
(
    application_type_id varchar(60) primary key,
    description         varchar(255),
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table application
(
    application_id      varchar(255) primary key not null,
    application_type_id varchar(60)              not null,
    module_id           varchar(255),
    permission_id       varchar(255),
    description         varchar(255),
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint application_permission foreign key (permission_id) references security_permission (permission_id),
    constraint application_application_type foreign key (application_type_id) references application_type (application_type_id),
    constraint application_application_module foreign key (module_id) references application (application_id)
);

-- Optional
create table notification_type
(
    notification_type_id varchar(100) primary key,
    notification_type_name varchar(200),
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table notifications
(
    id uuid primary key,
    content varchar(500),
    notification_type_id varchar(100),
    from_user varchar(60),
    to_user varchar(60),
    url varchar(200),
    status_id varchar(60) not null,
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint fk_notification_notification_type foreign key (notification_type_id) references notification_type (notification_type_id),
    constraint fk_notification_to_user foreign key (to_user) references user_login (user_login_id)
);

create TABLE status_item
(
    status_id          VARCHAR(60) NOT NULL,
    status_type_id     VARCHAR(60),
    status_code        VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_status_id PRIMARY KEY (status_id),
    CONSTRAINT fk_status_type_id FOREIGN KEY (status_type_id) REFERENCES status_type (status_type_id)
);

create table registered_affiliation
(
    affiliation_id varchar(200) primary key not null ,
    affiliation_name varchar(500),
    last_updated_stamp timestamp default current_timestamp,
    created_stamp timestamp default current_timestamp
);

create table user_register
(
    user_login_id varchar(60) primary key not null ,
    password varchar(100) not null ,
    email varchar(100) not null ,
    first_name varchar(100) not null ,
    middle_name varchar(100) not null ,
    last_name varchar(100) not null ,
    status_id varchar(60) ,
    registered_roles text not null ,
    last_updated_stamp timestamp default current_timestamp,
    created_stamp timestamp default current_timestamp,
    affiliations varchar(200)
);
