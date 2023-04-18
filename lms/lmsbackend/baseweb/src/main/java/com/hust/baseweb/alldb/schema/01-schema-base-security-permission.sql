create TABLE status_type
(
    status_type_id     VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_status_type PRIMARY KEY (status_type_id),
    CONSTRAINT status_type_parent FOREIGN KEY (parent_type_id) REFERENCES status_type (status_type_id)
);
create TABLE status
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
create TABLE party_type
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
create TABLE party
(
    party_id                    UUID      NOT NULL default uuid_generate_v1(),
    party_type_id               VARCHAR(60),
    name                        varchar(200),
    external_id                 VARCHAR(60),
    description                 TEXT,
    status_id                   VARCHAR(60),
    created_date                TIMESTAMP NULL,
    created_by_user_login       VARCHAR(255),
    last_modified_date          TIMESTAMP NULL,
    last_modified_by_user_login VARCHAR(255),
    is_unread                   BOOLEAN,
    last_updated_stamp          TIMESTAMP,
    created_stamp               TIMESTAMP          DEFAULT CURRENT_TIMESTAMP,
    party_code                  VARCHAR(255),
    CONSTRAINT pk_party PRIMARY KEY (party_id),
    CONSTRAINT party_status_item FOREIGN KEY (status_id) REFERENCES status (status_id),
    CONSTRAINT party_pty_typ FOREIGN KEY (party_type_id) REFERENCES party_type (party_type_id)
);
create TABLE person
(
    party_id           UUID      NOT NULL,
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

create TABLE user_login
(
    user_login_id            VARCHAR(255)        NOT NULL,
    current_password         VARCHAR(60),
    otp_secret               VARCHAR(60),
    client_token             VARCHAR(512),
    password_hint            TEXT,
    is_system                BOOLEAN,
    enabled                  BOOLEAN,
    has_logged_out           BOOLEAN,
    require_password_change  BOOLEAN,
    disabled_date_time       TIMESTAMP           NULL,
    successive_failed_logins INTEGER,
    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    otp_resend_number        INT       DEFAULT 0 NULL,
    party_id                 UUID,
    email                   varchar(100),
    CONSTRAINT pk_user_login PRIMARY KEY (user_login_id),
    CONSTRAINT user_party FOREIGN KEY (party_id) REFERENCES party (party_id)
);

alter table party
    add CONSTRAINT party_m_user_login FOREIGN KEY (last_modified_by_user_login) REFERENCES user_login (user_login_id);
alter table party
    add CONSTRAINT party_c_user_login FOREIGN KEY (created_by_user_login) REFERENCES user_login (user_login_id);



-- Drop table

-- DROP TABLE public.security_group;

CREATE TABLE public.security_group (
	group_id varchar(60) NOT NULL,
	description text NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	group_name varchar(100) NULL,
	CONSTRAINT pk_security_group PRIMARY KEY (group_id)
);



create TABLE security_permission
(
    permission_id      VARCHAR(100) NOT NULL,
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_security_permission PRIMARY KEY (permission_id)
);
create TABLE security_group_permission
(
    group_id           VARCHAR(60)  NOT NULL,
    permission_id      VARCHAR(100) NOT NULL,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_security_group_permission PRIMARY KEY (group_id, permission_id),
    CONSTRAINT sec_grp_perm_grp FOREIGN KEY (group_id) REFERENCES security_group (group_id),
    CONSTRAINT sec_grp_perm_perm FOREIGN KEY (permission_id) REFERENCES security_permission (permission_id)
);
create TABLE user_login_security_group
(
    user_login_id      VARCHAR(255) NOT NULL,
    group_id           VARCHAR(60)  NOT NULL,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_login_security_group PRIMARY KEY (user_login_id, group_id),
    CONSTRAINT user_secgrp_grp FOREIGN KEY (group_id) REFERENCES security_group (group_id),
    CONSTRAINT user_secgrp_user FOREIGN KEY (user_login_id) REFERENCES user_login (user_login_id)
);

create TABLE application_type
(
    application_type_id VARCHAR(60) NOT NULL,
    description         TEXT,
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_application_type PRIMARY KEY (application_type_id)
);

create TABLE application
(
    application_id      VARCHAR(255) NOT NULL,
    application_type_id VARCHAR(255) NOT NULL,
    module_id           VARCHAR(255),
    permission_id       VARCHAR(255),
    description         TEXT,
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_application PRIMARY KEY (application_id),
    CONSTRAINT application_application_type FOREIGN KEY (application_type_id) REFERENCES application_type (application_type_id),
    CONSTRAINT application_application_module FOREIGN KEY (module_id) REFERENCES application (application_id),
    CONSTRAINT application_permission FOREIGN KEY (permission_id) REFERENCES security_permission (permission_id)
);

create TABLE role_type
(
    role_type_id       VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_role_type_id PRIMARY KEY (role_type_id),
    CONSTRAINT fk_parent_type_id FOREIGN KEY (parent_type_id) REFERENCES role_type (role_type_id)
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

create TABLE enumeration_type
(
    enumeration_type_id VARCHAR(60) NOT NULL,
    parent_type_id      VARCHAR(60),
    description         TEXT,
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_enumeration_type_id PRIMARY KEY (enumeration_type_id),
    CONSTRAINT fk_parent_type_id FOREIGN KEY (parent_type_id) REFERENCES enumeration_type (enumeration_type_id)
);
create TABLE enumeration
(
    enum_id            VARCHAR(60) NOT NULL,
    enum_type_id       VARCHAR(60),
    enum_code          VARCHAR(60),
    sequence_id        VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_enum_id PRIMARY KEY (enum_id),
    CONSTRAINT fk_enum_type_id FOREIGN KEY (enum_type_id) REFERENCES enumeration_type (enumeration_type_id)
);


create TABLE uom_type
(
    uom_type_id        VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_oum_type_id PRIMARY KEY (uom_type_id),
    CONSTRAINT fk_parent_type_id FOREIGN KEY (parent_type_id) REFERENCES uom_type (uom_type_id)
);
create TABLE uom
(
    uom_id             VARCHAR(60) NOT NULL,
    uom_type_id        VARCHAR(60),
    abbreviation       VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_oum PRIMARY KEY (uom_id),
    CONSTRAINT fk_uom_type_id FOREIGN KEY (uom_type_id) REFERENCES uom_type (uom_type_id)
);
create TABLE content_type
(
    content_type_id    VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        VARCHAR(10000),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_content_type PRIMARY KEY (content_type_id),
    CONSTRAINT cntnt_type_parent FOREIGN KEY (parent_type_id) REFERENCES content_type (content_type_id)
);

create TABLE content
(
    content_id         UUID NOT NULL,
    content_type_id    VARCHAR(60),
    mime_type          VARCHAR(255),
    character_set      VARCHAR(100),
    url                VARCHAR(255),
    created_at         TIMESTAMP,
    last_updated_at    TIMESTAMP,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_content PRIMARY KEY (content_id),
    CONSTRAINT content_to_type FOREIGN KEY (content_type_id) REFERENCES content_type (content_type_id)
);
create TABLE product_content
(
    product_id         VARCHAR(60) NOT NULL,
    content_id         UUID        NOT NULL,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_content PRIMARY KEY (content_id, product_id),

    CONSTRAINT product_cnt_cnt FOREIGN KEY (content_id) REFERENCES content (content_id),
    CONSTRAINT product_cnt_product FOREIGN KEY (product_id) REFERENCES product (product_id)
);

create table party_relationship
(
    party_relationship_id uuid not null default uuid_generate_v1(),
    from_party_id         uuid not null,
    to_party_id           uuid not null,
    role_type_id          VARCHAR(60),
    from_date             TIMESTAMP,
    thru_date             TIMESTAMP,
    description           TEXT,
    last_updated_stamp    TIMESTAMP,
    created_stamp         TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_party_relationship primary key (party_relationship_id),
    constraint fk_party_relationship_from_party_id foreign key (from_party_id) references party (party_id),
    constraint fk_party_relationship_to_party_id foreign key (to_party_id) references party (party_id),
    constraint fk_party_relationship_role_type_id foreign key (role_type_id) references role_type (role_type_id)
);



-- Drop table

-- DROP TABLE public.user_register;

create table registered_affiliation(
    affiliation_id varchar(200),
    affiliation_name varchar(500),
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    constraint pk_registered_affiliation primary key  (affiliation_id)
);
create TABLE public.user_register (
	user_login_id varchar(60) NOT NULL,
	"password" varchar(100) NOT NULL,
	email varchar(100) NOT NULL,
	first_name varchar(100) NOT NULL,
	middle_name varchar(100) NOT NULL,
	last_name varchar(100) NOT NULL,
	status_id varchar(60) NULL,
	registered_roles text NOT NULL,
	affiliations text,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT pk_register__login PRIMARY KEY (user_login_id),
	CONSTRAINT fk_register_status FOREIGN KEY (status_id) REFERENCES status_item(status_id)
);
