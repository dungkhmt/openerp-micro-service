CREATE TABLE facility_type
(
    facility_type_id   VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_facility_type_id PRIMARY KEY (facility_type_id),
    CONSTRAINT fk_parent_type_id FOREIGN KEY (parent_type_id) REFERENCES facility_type (facility_type_id)

);

CREATE TABLE facility
(
    facility_id        VARCHAR(60) NOT NULL,
    facility_type_id   VARCHAR(60),
    parent_facility_id VARCHAR(60),
    facility_name      VARCHAR(100),
    contact_mech_id    UUID,
    opened_date        TIMESTAMP,
    closed_date        TIMESTAMP,
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_facility_id PRIMARY KEY (facility_id),
    constraint fk_facility_contact_mech_id foreign key (contact_mech_id) references postal_address (contact_mech_id),
    CONSTRAINT fk_facility_type_id FOREIGN KEY (facility_type_id) REFERENCES facility_type (facility_type_id),
    CONSTRAINT fk_parent_facility_id FOREIGN KEY (parent_facility_id) REFERENCES facility (facility_id)
);

create table facility_role
(
    facility_role_id uuid default uuid_generate_v1(),
    user_login_id    varchar(60),
    facility_id      varchar(60),
    role_type_id     varchar(60),
    from_date        timestamp,
    thru_date        timestamp,
    constraint pk_facility_role primary key (facility_role_id),
    constraint fk_facility_role_user_login_id foreign key (user_login_id) references user_login (user_login_id),
    constraint fk_facility_role_facility_id foreign key (facility_id) references facility (facility_id),
    constraint fk_facility_role_role_type_id foreign key (role_type_id) references role_type (role_type_id)
);
