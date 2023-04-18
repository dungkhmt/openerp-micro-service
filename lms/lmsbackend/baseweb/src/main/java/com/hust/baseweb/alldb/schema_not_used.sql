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
    party_id                    UUID      NOT NULL default uuid_generate_v1(),
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
    created_stamp               TIMESTAMP          DEFAULT CURRENT_TIMESTAMP,
    party_code                  VARCHAR(255),
    CONSTRAINT pk_party PRIMARY KEY (party_id),
    CONSTRAINT party_status_item FOREIGN KEY (status_id) REFERENCES status (status_id),
    CONSTRAINT party_pty_typ FOREIGN KEY (party_type_id) REFERENCES party_type (party_type_id)
);
CREATE TABLE person
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

CREATE TABLE user_login
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
    CONSTRAINT pk_user_login PRIMARY KEY (user_login_id),
    CONSTRAINT user_party FOREIGN KEY (party_id) REFERENCES party (party_id)
);

ALTER TABLE party
    ADD CONSTRAINT party_m_user_login FOREIGN KEY (last_modified_by_user_login) REFERENCES user_login (user_login_id);
ALTER TABLE party
    ADD CONSTRAINT party_c_user_login FOREIGN KEY (created_by_user_login) REFERENCES user_login (user_login_id);

CREATE TABLE security_group
(
    group_id           VARCHAR(60) NOT NULL,
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_security_group PRIMARY KEY (group_id)
);
CREATE TABLE security_permission
(
    permission_id      VARCHAR(100) NOT NULL,
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_security_permission PRIMARY KEY (permission_id)
);
CREATE TABLE security_group_permission
(
    group_id           VARCHAR(60)  NOT NULL,
    permission_id      VARCHAR(100) NOT NULL,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_security_group_permission PRIMARY KEY (group_id, permission_id),
    CONSTRAINT sec_grp_perm_grp FOREIGN KEY (group_id) REFERENCES security_group (group_id),
    CONSTRAINT sec_grp_perm_perm FOREIGN KEY (permission_id) REFERENCES security_permission (permission_id)
);
CREATE TABLE user_login_security_group
(
    user_login_id      VARCHAR(255) NOT NULL,
    group_id           VARCHAR(60)  NOT NULL,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_login_security_group PRIMARY KEY (user_login_id, group_id),
    CONSTRAINT user_secgrp_grp FOREIGN KEY (group_id) REFERENCES security_group (group_id),
    CONSTRAINT user_secgrp_user FOREIGN KEY (user_login_id) REFERENCES user_login (user_login_id)
);

CREATE TABLE application_type
(
    application_type_id VARCHAR(60) NOT NULL,
    description         TEXT,
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_application_type PRIMARY KEY (application_type_id)
);

CREATE TABLE application
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


create table department
(
    department_id           UUID not null default uuid_generate_v1(),
    department_name         VARCHAR(100),
    start_date              TIMESTAMP,
    created_by_userLogin_id VARCHAR(255),
    last_updated_stamp      TIMESTAMP,
    created_stamp           TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_department primary key (department_id),
    constraint fk_department_create_by_user_login_id foreign key (created_by_userLogin_id) references user_login (user_login_id)
);

create table geo_type
(
    geo_type_id        VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_geo_type primary key (geo_type_id)

);

create table geo
(
    geo_id             VARCHAR(60) NOT NULL,
    geo_type_id        VARCHAR(60),
    geo_name           VARCHAR(100),
    geo_code           VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_geo primary key (geo_id),
    constraint fk_geo_geo_type_id foreign key (geo_type_id) references geo_type (geo_type_id)
);

create table geo_point
(
    geo_point_id       UUID        NOT NULL default uuid_generate_v1(),

    longitude          VARCHAR(30) not null,
    latitude           VARCHAR(30) not null,

    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    constraint pk_geo_point primary key (geo_point_id)

);

create table distance_travel_time_geo_points
(
    from_geo_point_id  uuid not null
        constraint distance_travel_time_geo_points_geo_point_geo_point_id_from_fk
            references geo_point,
    to_geo_point_id    uuid not null
        constraint distance_travel_time_geo_points_geo_point_geo_point_id_to_fk
            references geo_point,
    distance           numeric,
    travel_time        numeric,
    last_updated_stamp timestamp,
    created_stamp      timestamp default current_timestamp
);

create table distance_travel_time_postal_address
(
    from_contact_mech_id     uuid not null,
    to_contact_mech_id       uuid not null,
    distance                 numeric,
    travel_time              numeric,
    travel_time_truck        numeric,
    travel_time_motorbike    numeric,
    updated_by_user_login_id VARCHAR(60),
    updated_date             TIMESTAMP,
    source_enum_id           VARCHAR(60),
    last_updated_stamp       timestamp,
    created_stamp            timestamp default current_timestamp,
    constraint pk_distance_travel_time_postal_address primary key (from_contact_mech_id, to_contact_mech_id),
    constraint fk_distance_travel_time_postal_address_from foreign key (from_contact_mech_id) references postal_address (contact_mech_id),
    constraint fk_distance_travel_time_postal_address_to foreign key (to_contact_mech_id) references postal_address (contact_mech_id),
    constraint fk_distance_travel_time_postal_address_source_enum_id foreign key (source_enum_id) references enumeration (enum_id),
    constraint fk_distance_travel_time_postal_address_updated_by_user_login foreign key (updated_by_user_login_id) references user_login (user_login_id)
);


create unique index distance_travel_time_geo_points_from_geo_point_id_to_geo_point_id_uindex
    on distance_travel_time_geo_points (from_geo_point_id, to_geo_point_id);

alter table distance_travel_time_geo_points
    add constraint distance_travel_time_geo_points_pk
        primary key (from_geo_point_id, to_geo_point_id);



create table postal_address
(
    contact_mech_id       UUID NOT NULL default uuid_generate_v1(),
    location_code         VARCHAR(60),
    address               VARCHAR(200),
    postal_code           VARCHAR(60),
    geo_point_id          UUID,
    country_geo_id        VARCHAR(60),
    state_province_geo_id VARCHAR(60),
    city                  VARCHAR(200),
    max_load_weight       numeric,
    last_updated_stamp    TIMESTAMP,
    created_stamp         TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_postal_address primary key (contact_mech_id),
    constraint fk_postal_address_geo_point_id foreign key (geo_point_id) references geo_point (geo_point_id),
    constraint fk_postal_address_country_geo_id foreign key (country_geo_id) references geo (geo_id),
    constraint fk_postal_address_state_province_geo_id foreign key (state_province_geo_id) references geo (geo_id)
);

create table contact_mech_purpose_type
(
    contact_mech_purpose_type_id VARCHAR(60) NOT NULL,
    description                  TEXT,
    last_updated_stamp           TIMESTAMP,
    created_stamp                TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_contact_mech_purpose_type primary key (contact_mech_purpose_type_id)
);

create table party_contact_mech_purpose
(
    party_id                     UUID NOT NULL,
    contact_mech_id              UUID,
    contact_mech_purpose_type_id VARCHAR(60),
    from_date                    TIMESTAMP,
    thru_date                    TIMESTAMP,
    last_updated_stamp           TIMESTAMP,
    created_stamp                TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_party_contact_mech_purpose primary key (party_id, contact_mech_id, contact_mech_purpose_type_id, from_date),
    constraint fk_party_contact_mech_purpose_party_id foreign key (party_id) references party (party_id),
    constraint fk_party_contact_mech_purpose_contact_mech_id foreign key (contact_mech_id) references postal_address (contact_mech_id),
    constraint fk_party_contact_mech_purpose_contact_mech_purpose_type_id foreign key (contact_mech_purpose_type_id) references contact_mech_purpose_type (contact_mech_purpose_type_id)
);

CREATE TABLE role_type
(
    role_type_id       VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_role_type_id PRIMARY KEY (role_type_id),
    CONSTRAINT fk_parent_type_id FOREIGN KEY (parent_type_id) REFERENCES role_type (role_type_id)
);

CREATE TABLE status_item
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

CREATE TABLE party_customer
(
    party_id           UUID NOT NULL,
    customer_code      VARCHAR(100),
    customer_name      VARCHAR(200),
    status_id          VARCHAR(60),
    party_type_id      VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_party_customer_party_id PRIMARY KEY (party_id),
    CONSTRAINT fp_party_customer_party_id FOREIGN KEY (party_id) REFERENCES party (party_id),
    CONSTRAINT fp_party_customer_party_type_id FOREIGN KEY (party_type_id) REFERENCES party_type (party_type_id),
    CONSTRAINT fp_party_customer_status_id FOREIGN KEY (status_id) REFERENCES status_item (status_id)
);

create table party_distributor
(
    party_id           UUID NOT NULL,
    distributor_code   VARCHAR(100),
    distributor_name   VARCHAR(100),
    status_id          VARCHAR(60),
    party_type_id      VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_party_distributor_party_id PRIMARY KEY (party_id),
    CONSTRAINT fp_party_distributor_party_id FOREIGN KEY (party_id) REFERENCES party (party_id),
    CONSTRAINT fp_party_distributor_party_type_id FOREIGN KEY (party_type_id) REFERENCES party_type (party_type_id),
    CONSTRAINT fp_party_distributor_status_id FOREIGN KEY (status_id) REFERENCES status_item (status_id)
);

create table party_salesman
(
    party_id           UUID NOT NULL default uuid_generate_v1(),
    status_id          VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_party_salesman primary key (party_id),
    constraint fk_party_salesman_status_id foreign key (status_id) references status_item (status_id)
);



create table customer_salesman
(
    customer_salesman_id UUID NOT NULL default uuid_generate_v1(),
    party_customer_id    UUID NOT NULL,
    party_salesman_id    UUID NOT NULL,
    from_date            TIMESTAMP,
    thru_date            TIMESTAMP,
    constraint pk_customer_salesman primary key (customer_salesman_id),
    constraint fk_customer_salesman_customer foreign key (party_customer_id) references party_customer (party_id),
    constraint fk_customer_salesman_salesman foreign key (party_salesman_id) references party_salesman (party_id)
);

create table customer_salesman_vendor
(
    customer_salesman_vendor_id UUID NOT NULL default uuid_generate_v1(),
    party_customer_id           UUID NOT NULL,
    party_salesman_id           UUID NOT NULL,
    party_vendor_id             UUID NOT NULL,
    from_date                   TIMESTAMP,
    thru_date                   TIMESTAMP,
    constraint pk_customer_salesman_vendor primary key (customer_salesman_vendor_id),
    constraint fk_customer_salesman_vendor_customer foreign key (party_customer_id) references party (party_id),
    constraint fk_customer_salesman_vendor_salesman foreign key (party_salesman_id) references party (party_id),
    constraint fk_customer_salesman_vendor_vendor foreign key (party_vendor_id) references party (party_id)
);


create table sales_route_config
(
    sales_route_config_id UUID NOT NULL default uuid_generate_v1(),
    days                  VARCHAR(60),
    repeat_week           Integer,
    status_id             VARCHAR(60),
    description           TEXT,
    last_updated_stamp    TIMESTAMP,
    created_stamp         TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_sales_route_config primary key (sales_route_config_id),
    constraint fk_sales_route_config_status foreign key (status_id) references status_item (status_id)
);
create table sales_route_planning_period
(
    sales_route_planning_period_id UUID NOT NULL default uuid_generate_v1(),
    from_date                      VARCHAR(60),
    to_date                        VARCHAR(60),
    created_by                     VARCHAR(60),
    description                    TEXT,
    status_id                      VARCHAR(60),
    constraint pk_sales_planning_period primary key (sales_route_planning_period_id),
    constraint fk_sales_planning_period_created_by foreign key (created_by) references user_login (user_login_id),
    constraint fk_sales_planning_period_status foreign key (status_id) references status_item (status_id)
);

create table sales_route_config_customer
(
    sales_route_config_customer_id UUID NOT NULL default uuid_generate_v1(),
    sales_route_planning_period_id UUID not null,

    sales_route_config_id          UUID NOT NULL,
    customer_salesman_vendor_id    UUID NOT NULL,
    status_id                      VARCHAR(60),
    start_execute_week             int,
    number_days_per_week           int,
    repeat_week                    int,
    last_updated_stamp             TIMESTAMP,
    created_stamp                  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_sales_route_config_customer primary key (sales_route_config_customer_id),
    constraint fk_sales_route_config_customer_sales_route_config_id foreign key (sales_route_config_id) references sales_route_config (sales_route_config_id),
    constraint fk_sales_route_config_customer_sales_route_planning_period_id foreign key (sales_route_planning_period_id) references sales_route_planning_period (sales_route_planning_period_id),
    constraint fk_sales_route_config_customer_customer_salesman_vendor_id foreign key (customer_salesman_vendor_id) references customer_salesman_vendor (customer_salesman_vendor_id)
);

create table sales_route_detail
(
    sales_route_detail_id          UUID NOT NULL,
    party_salesman_id              UUID NOT NULL,
    party_customer_id              UUID NOT NULL,
    party_distributor              UUID not null,
    sequence                       Integer,
    execute_date                   VARCHAR(60),
    sales_route_config_customer_id UUID NOT NULL,
    sales_route_planning_period_id UUID,
    last_updated_stamp             TIMESTAMP,
    created_stamp                  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_sales_route_detail primary key (sales_route_detail_id),
    constraint fk_sales_route_detail_sales_route_config_customer foreign key (sales_route_config_customer_id) references sales_route_config_customer (sales_route_config_customer_id),
    constraint fk_sales_route_detail_party_customer foreign key (party_customer_id) references party_customer (party_id),
    constraint fk_sales_route_detail_salesman_id foreign key (party_salesman_id) references party_salesman (party_id),
    constraint fk_sales_route_detail_sales_route_planning_period_id foreign key (sales_route_planning_period_id) references sales_route_planning_period (sales_route_planning_period_id)
);

create table salesman_checkin_history
(
    salesman_checkin_history_id UUID NOT NULL default uuid_generate_v1(),
    user_login_id               VARCHAR(60),
    party_customer_id           UUID,
    location                    VARCHAR(60),
    check_in_action             VARCHAR(1),
    time_point                  TIMESTAMP,
    last_updated_stamp          TIMESTAMP,
    created_stamp               TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_salesman_checkin_history primary key (salesman_checkin_history_id),
    constraint fk_salesman_checkin_history_user_login_id foreign key (user_login_id) references user_login (user_login_id),
    constraint fk_salesman_checkin_history_party_customer_id foreign key (party_customer_id) references party_customer (party_id)
);

CREATE TABLE enumeration_type
(
    enumeration_type_id VARCHAR(60) NOT NULL,
    parent_type_id      VARCHAR(60),
    description         TEXT,
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_enumeration_type_id PRIMARY KEY (enumeration_type_id),
    CONSTRAINT fk_parent_type_id FOREIGN KEY (parent_type_id) REFERENCES enumeration_type (enumeration_type_id)
);
CREATE TABLE enumeration
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


CREATE TABLE uom_type
(
    uom_type_id        VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_oum_type_id PRIMARY KEY (uom_type_id),
    CONSTRAINT fk_parent_type_id FOREIGN KEY (parent_type_id) REFERENCES uom_type (uom_type_id)
);
CREATE TABLE uom
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
CREATE TABLE product_store_group
(
    product_store_group_id VARCHAR(60) NOT NULL,
    description            TEXT,
    last_updated_stamp     TIMESTAMP,
    created_stamp          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_store_group_id PRIMARY KEY (product_store_group_id)
);

CREATE TABLE product_store
(
    product_store_id       VARCHAR(60) NOT NULL,
    store_name             VARCHAR(100),
    product_store_group_id VARCHAR(60),
    owner_party_id         UUID,
    description            TEXT,
    last_updated_stamp     TIMESTAMP,
    created_stamp          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_store_id PRIMARY KEY (product_store_id),
    constraint fk_product_store_owner_party_id foreign key (owner_party_id) references party (party_id),
    constraint fk_product_store_product_store_group foreign key (product_store_group_id) references product_store_group (product_store_group_id)
);

CREATE TABLE product_type
(
    product_type_id    VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_type_id PRIMARY KEY (product_type_id),
    CONSTRAINT fk_parent_type_id FOREIGN KEY (parent_type_id) REFERENCES product_type (product_type_id)

);
CREATE TABLE product
(
    product_id                    VARCHAR(60) NOT NULL,
    product_type_id               VARCHAR(60),
    product_name                  VARCHAR(200),
    weight                        numeric,
    hs_thu                        int,
    hs_pal                        int,
    introductionDate              TIMESTAMP,
    quantity_uom_id               VARCHAR(60),
    weight_uom_id                 VARCHAR(60),
    width_uom_id                  VARCHAR(60),
    length_uom_id                 VARCHAR(60),
    height_uom_id                 VARCHAR(60),
    created_by_user_login_id      VARCHAR(60),
    product_transport_category_id varchar(60),

    description                   TEXT,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_id PRIMARY KEY (product_id),
    CONSTRAINT fk_product_type_id FOREIGN KEY (product_type_id) REFERENCES product_type (product_type_id),
    CONSTRAINT fk_created_by_user_login_id FOREIGN KEY (created_by_user_login_id) REFERENCES user_login (user_login_id),
    CONSTRAINT fk_quantity_uom_id FOREIGN KEY (quantity_uom_id) REFERENCES uom (uom_id),
    CONSTRAINT fk_weight_uom_id FOREIGN KEY (weight_uom_id) REFERENCES uom (uom_id),
    CONSTRAINT fk_length_uom_id FOREIGN KEY (length_uom_id) REFERENCES uom (uom_id),
    CONSTRAINT fk_width_uom_id FOREIGN KEY (width_uom_id) REFERENCES uom (uom_id),
    CONSTRAINT fk_height_uom_id FOREIGN KEY (height_uom_id) REFERENCES uom (uom_id),
    constraint fk_vehicle_type_product_transport_category_id foreign key (product_transport_category_id) references enumeration (enum_id)
);

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
    product_store_id   VARCHAR(60),
    opened_date        TIMESTAMP,
    closed_date        TIMESTAMP,
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_facility_id PRIMARY KEY (facility_id),
    constraint fk_facility_contact_mech_id foreign key (contact_mech_id) references postal_address (contact_mech_id),
    CONSTRAINT fk_facility_type_id FOREIGN KEY (facility_type_id) REFERENCES facility_type (facility_type_id),
    CONSTRAINT fk_parent_facility_id FOREIGN KEY (parent_facility_id) REFERENCES facility (facility_id),
    CONSTRAINT fk_product_store_id FOREIGN KEY (product_store_id) REFERENCES product_store (product_store_id)
);

CREATE TABLE inventory_item
(
    inventory_item_id          UUID NOT NULL default uuid_generate_v1(),
    product_id                 VARCHAR(60),
    status_id                  VARCHAR(60),
    datetime_received          TIMESTAMP,
    datetime_manufactured      TIMESTAMP,
    expire_date                TIMESTAMP,
    activation_valid_thru      TIMESTAMP,
    facility_id                VARCHAR(60),
    lot_id                     VARCHAR(60),
    uom_id                     VARCHAR(60),
    unit_cost                  DECIMAL(18, 6),
    currency_uom_id            VARCHAR(60),
    quantity_on_hand_total     DECIMAL(18, 6),
    available_to_promise_total DECIMAL(18, 6),
    description                TEXT,
    last_updated_stamp         TIMESTAMP,
    created_stamp              TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_inventory_item_id PRIMARY KEY (inventory_item_id),
    CONSTRAINT fk_inventory_item_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
    CONSTRAINT fk_inventory_item_status_id FOREIGN KEY (status_id) REFERENCES status_item (status_id),
    CONSTRAINT fk_inventory_item_currency_uom_id FOREIGN KEY (currency_uom_id) REFERENCES uom (uom_id),
    CONSTRAINT fk_inventory_item_facility_id FOREIGN KEY (facility_id) REFERENCES facility (facility_id)
);



CREATE TABLE inventory_item_detail
(
    inventory_item_detail_id UUID NOT NULL default uuid_generate_v1(),
    inventory_item_id        UUID NOT NULL,
    effective_date           TIMESTAMP,
    quantity_on_hand_diff    DECIMAL(18, 6),
    order_id                 VARCHAR(60),
    order_item_seq_id        VARCHAR(60),
    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_inventory_item_detail PRIMARY KEY (inventory_item_detail_id),
    CONSTRAINT fk_inventory_item_detail_inventory_item_id FOREIGN KEY (inventory_item_id) REFERENCES inventory_item (inventory_item_id),
    CONSTRAINT fk_inventory_item_detail_order_id_order_item_seq_id FOREIGN KEY (order_id, order_item_seq_id) REFERENCES order_item (order_id, order_item_seq_id)
);



CREATE TABLE product_price
(
    product_price_id         UUID        NOT NULL default uuid_generate_v1(),
    product_id               VARCHAR(60) NOT NULL,
    currency_uom_id          VARCHAR(60),
    from_date                TIMESTAMP,
    thru_date                TIMESTAMP,
    product_store_group_id   VARCHAR(60),
    tax_in_price             VARCHAR(1),
    price                    DECIMAL(18, 3),
    created_by_user_login_id VARCHAR(60),
    created_date             TIMESTAMP,
    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_price PRIMARY KEY (product_price_id),
    CONSTRAINT fk_product_price_currency_uom_id FOREIGN KEY (currency_uom_id) REFERENCES uom (uom_id),
    CONSTRAINT fk_product_price_created_by_user_login_id FOREIGN KEY (created_by_user_login_id) REFERENCES user_login (user_login_id),
    CONSTRAINT fk_product_price_product_store_group_id FOREIGN KEY (product_store_group_id) REFERENCES product_store_group (product_store_group_id)
);



CREATE TABLE product_facility
(
    product_id           VARCHAR(60) NOT NULL,
    facility_id          VARCHAR(60) NOT NULL,
    atp_inventory_count  INT,
    last_inventory_count INT,
    description          TEXT,
    last_updated_stamp   TIMESTAMP,
    created_stamp        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_facility PRIMARY KEY (product_id, facility_id),
    CONSTRAINT fk_product_facility_facility_id FOREIGN KEY (facility_id) REFERENCES facility (facility_id),
    CONSTRAINT fk_product_facility_product_id FOREIGN KEY (product_id) REFERENCES product (product_id)
);

CREATE TABLE sales_channel
(
    sales_channel_id   VARCHAR(60) NOT NULL,
    sales_channel_name VARCHAR(100),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_sales_channel_id PRIMARY KEY (sales_channel_id)
);

CREATE TABLE order_type
(
    order_type_id      VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_order_type_id PRIMARY KEY (order_type_id),
    CONSTRAINT fk_parent_type FOREIGN KEY (parent_type_id) REFERENCES order_type (order_type_id)
);



CREATE TABLE order_header
(
    order_id             VARCHAR(60) NOT NULL,
    order_type_id        VARCHAR(60),
    original_facility_id VARCHAR(60),
    product_store_id     VARCHAR(60),
    sales_channel_id     VARCHAR(60),
    created_by           VARCHAR(60),
    order_date           TIMESTAMP,
    currency_uom_id      VARCHAR(60),
    ship_to_address_id   UUID,
    grand_total          DECIMAL(18, 2),
    description          TEXT,
    exported             boolean,
    party_customer_id    uuid,
    vendor_id            uuid,
    sale_man_id          varchar(60),
    last_updated_stamp   TIMESTAMP,
    created_stamp        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_order PRIMARY KEY (order_id),
    CONSTRAINT fk_order_type_id FOREIGN KEY (order_type_id) REFERENCES order_type (order_type_id),
    CONSTRAINT fk_original_facility_id FOREIGN KEY (original_facility_id) REFERENCES facility (facility_id),
    constraint fk_order_address_id foreign key (ship_to_address_id) references postal_address (contact_mech_id),
    CONSTRAINT fk_product_store_id FOREIGN KEY (product_store_id) REFERENCES facility (facility_id),
    CONSTRAINT fk_currency_uom_id FOREIGN KEY (currency_uom_id) REFERENCES uom (uom_id),
    CONSTRAINT fk_sales_channel_id FOREIGN KEY (sales_channel_id) REFERENCES sales_channel (sales_channel_id),
    constraint fk_party_customer_id foreign key (party_customer_id) references party_customer (party_id)
);
CREATE TABLE order_item_type
(
    order_item_type_id VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60) NOT NULL,
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_order_item_type_id PRIMARY KEY (order_item_type_id),
    CONSTRAINT fk_parent_type_id FOREIGN KEY (parent_type_id) REFERENCES order_item_type (order_item_type_id)
);

CREATE TABLE order_item
(
    order_id           VARCHAR(60) NOT NULL,
    order_item_seq_id  VARCHAR(60),
    order_item_type_id VARCHAR(60),
    product_id         VARCHAR(60),

    unit_price         numeric,
    quantity           int,
    exported_quantity  int       default 0,
    status_id          VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_order_item_id PRIMARY KEY (order_id, order_item_seq_id),
    CONSTRAINT fk_order_item_type_id FOREIGN KEY (order_item_type_id) REFERENCES order_item_type (order_item_type_id),
    CONSTRAINT fk_order_item_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
    CONSTRAINT fk_order_item_order_id FOREIGN KEY (order_id) REFERENCES order_header (order_id),
    CONSTRAINT fk_status_id FOREIGN KEY (status_id) REFERENCES status_item (status_id)
);

CREATE TABLE order_role
(
    order_id           VARCHAR(60),
    party_id           UUID,
    role_type_id       VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_order_role PRIMARY KEY (order_id, party_id, role_type_id),
    CONSTRAINT fk_order_role_order_id FOREIGN KEY (order_id) REFERENCES order_header (order_id),
    CONSTRAINT fk_order_role_party_id FOREIGN KEY (party_id) REFERENCES party (party_id),
    CONSTRAINT fk_order_role_role_type_id FOREIGN KEY (role_type_id) REFERENCES role_type (role_type_id)
);


CREATE TABLE order_status
(
    order_status_id      VARCHAR(60),
    status_id            VARCHAR(60),
    order_id             VARCHAR(60),
    status_datetime      TIMESTAMP,
    status_user_login_id VARCHAR(60),
    last_updated_stamp   TIMESTAMP,
    created_stamp        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_order_status_id PRIMARY KEY (order_status_id),
    CONSTRAINT fk_order_status_status_id FOREIGN KEY (status_id) REFERENCES status_item (status_id),
    CONSTRAINT fk_order_status_order_id FOREIGN KEY (order_id) REFERENCES order_header (order_id),
    CONSTRAINT fk_status_user_login_id FOREIGN KEY (status_user_login_id) REFERENCES user_login (user_login_id)
);



create table product_promo_type
(
    product_promo_type_id VARCHAR(60) NOT NULL,
    parent_type_id        VARCHAR(60),
    last_updated_stamp    TIMESTAMP,
    created_stamp         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_product_promo_type primary key (product_promo_type_id),
    constraint fk_product_promo_parent_type foreign key (parent_type_id) references product_promo_type (product_promo_type_id)
);

CREATE TABLE product_promo
(
    product_promo_id      UUID         NOT NULL default uuid_generate_v1(),
    promo_name            varchar(100) NULL,
    promo_text            varchar(255) NULL,
    product_promo_type_id VARCHAR(60),
    from_date             TIMESTAMP,
    thru_date             TIMESTAMP,
    last_updated_stamp    TIMESTAMP,
    created_stamp         TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    constraint pk_product_promo primary key (product_promo_id),
    constraint fk_product_promo_product_promo_type_id foreign key (product_promo_type_id) references product_promo_type (product_promo_type_id)
);



CREATE TABLE product_promo_rule
(
    product_promo_rule_id      UUID         NOT NULL default uuid_generate_v1(),
    product_promo_id           UUID         NOT NULL,
    product_promo_rule_enum_id varchar(60)  NOT NULL,
    rule_name                  varchar(100) NULL,
    json_params                TEXT,
    last_updated_stamp         timestamptz  NULL,
    last_updated_tx_stamp      timestamptz  NULL,
    created_stamp              timestamptz  NULL,
    created_tx_stamp           timestamptz  NULL,
    CONSTRAINT pk_product_promo_rule PRIMARY KEY (product_promo_rule_id),
    CONSTRAINT fk_product_promo_rule_product_promo FOREIGN KEY (product_promo_id) REFERENCES product_promo (product_promo_id),
    constraint fk_product_promo_rule_enum foreign key (product_promo_rule_enum_id) references enumeration (enum_id)
);



CREATE TABLE product_promo_product
(
    product_promo_rule_id UUID        NOT NULL,
    product_id            varchar(60) NOT NULL,
    last_updated_stamp    TIMESTAMP,
    created_stamp         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_promo_product PRIMARY KEY (product_promo_rule_id, product_id),
    CONSTRAINT fk_product_promo_product_rule FOREIGN KEY (product_promo_rule_id) REFERENCES product_promo_rule (product_promo_rule_id),
    CONSTRAINT fk_product_promo_product_product FOREIGN KEY (product_id) REFERENCES product (product_id)
);

CREATE TABLE tax_authority_rate_type
(
    tax_auth_rate_type_id varchar(60) not null,
    description           text,
    last_updated_stamp    TIMESTAMP,
    created_stamp         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_tax_auth_rate_type primary key (tax_auth_rate_type_id)
);

CREATE TABLE tax_authority_rate_product
(
    tax_authority_rate_seq_id UUID not null default uuid_generate_v1(),
    tax_auth_rate_type_id     varchar(60),
    product_id                varchar(60),
    tax_percentage            decimal(18, 6),
    from_date                 TIMESTAMP,
    thru_date                 TIMESTAMP     DEFAULT NULL,
    description               text,
    last_updated_stamp        TIMESTAMP,
    created_stamp             TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_tax_auth_rate_prod primary key (tax_authority_rate_seq_id),
    constraint fk_tax_auth_rate_prod_product_id foreign key (product_id) references product (product_id),
    constraint fk_tax_auth_rate_prod_tax_auth_type_id foreign key (tax_auth_rate_type_id) references tax_authority_rate_type (tax_auth_rate_type_id)
);

CREATE TABLE order_adjustment_type
(
    order_adjustment_type_id varchar(60) NOT NULL,
    parent_type_id           varchar(60) DEFAULT NULL,
    description              TEXT,
    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    constraint pk_order_adjustment_type PRIMARY KEY (order_adjustment_type_id),
    constraint fk_order_adjustment_type foreign key (parent_type_id) references order_adjustment_type (order_adjustment_type_id)
);

CREATE TABLE order_adjustment
(
    order_adjustment_id       UUID NOT NULL  default uuid_generate_v1(),
    order_adjustment_type_id  varchar(60),
    order_id                  VARCHAR(60),
    order_item_seq_id         VARCHAR(60),
    product_promo_rule_id     UUID NOT NULL,
    product_id                VARCHAR(60),
    tax_authority_rate_seq_id UUID,
    description               TEXT,
    amount                    decimal(18, 3) DEFAULT NULL,
    quantity                  Integer,
    last_updated_stamp        TIMESTAMP,
    created_stamp             TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    constraint pk_order_adjustment primary key (order_adjustment_id),
    constraint fk_order_adjustment_order_adjustment_type_id foreign key (order_adjustment_type_id) references order_adjustment_type (order_adjustment_type_id),
    constraint fk_order_adjustment_order_order_item foreign key (order_id, order_item_seq_id) references order_item (order_id, order_item_seq_id),
    constraint fk_order_adjustment_tax_auth_rate_type_id foreign key (tax_authority_rate_seq_id) references tax_authority_rate_product (tax_authority_rate_seq_id),
    constraint fk_order_adjustment_product_promo_product foreign key (product_promo_rule_id, product_id) references product_promo_product (product_promo_rule_id, product_id)
);


create table vehicle_type
(
    vehicle_type_id               VARCHAR(60) NOT NULL,
    capacity                      numeric,
    long                          Integer,
    width                         Integer,
    height                        Integer,
    pallet                        numeric,
    product_transport_category_id VARCHAR(60),
    description                   TEXT,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_vehicle_type primary key (vehicle_type_id),
    constraint fk_vehicle_type_product_transport_category_id foreign key (product_transport_category_id) references enumeration (enum_id)
);

create table vehicle
(
    vehicle_id                    VARCHAR(60) NOT NULL,
    vehicle_type_id               VARCHAR(60),
    capacity                      numeric,
    long                          Integer,
    width                         Integer,
    height                        Integer,
    pallet                        numeric,
    status_id                     VARCHAR(60),
    product_transport_category_id VARCHAR(60),
    priority                      int,
    description                   TEXT,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_vehicle_id primary key (vehicle_id),
    constraint fk_vehicle_vehicle_type foreign key (vehicle_type_id) references vehicle_type (vehicle_type_id),
    constraint fk_vehicle_status_id foreign key (status_id) references status_item (status_id),
    constraint fk_vehicle_type_product_transport_category_id foreign key (product_transport_category_id) references enumeration (enum_id)
);

create table vehicle_maintenance_history
(
    vehicle_maintenance_history_id UUID        NOT NULL default uuid_generate_v1(),
    vehicle_id                     VARCHAR(60) NOT NULL,
    maintenance_date               TIMESTAMP,
    thru_date                      TIMESTAMP,
    capacity                       numeric,
    long                           Integer,
    width                          Integer,
    height                         Integer,
    pallet                         numeric,
    description                    TEXT,
    last_updated_stamp             TIMESTAMP,
    created_stamp                  TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    constraint pk_vehicle_maintenance_history primary key (vehicle_maintenance_history_id),
    constraint fk_vehicle_maintenance_history_vehicle_id foreign key (vehicle_id) references vehicle (vehicle_id)
);

create table vehicle_forbidden_geo_point
(
    vehicle_forbidden_geo_point_id UUID NOT NULL default uuid_generate_v1(),
    vehicle_id                     VARCHAR(60),
    geo_point_id                   UUID,
    from_date                      TIMESTAMP,
    thru_date                      TIMESTAMP,
    last_updated_stamp             TIMESTAMP,
    created_stamp                  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_vehicle_forbidden_geo_point primary key (vehicle_forbidden_geo_point_id),
    constraint fk_vehicle_forbidden_geo_point_vehicle_id foreign key (vehicle_id) references vehicle (vehicle_id),
    constraint fk_vehicle_forbidden_geo_point_geo_point_id foreign key (geo_point_id) references geo_point (geo_point_id)
);


create table vehicle_location_priority
(
    vehicle_location_priority_id UUID NOT NULL default uuid_generate_v1(),
    vehicle_id                   VARCHAR(60),
    contact_mech_id              UUID,
    priority                     Integer,
    from_date                    TIMESTAMP,
    thru_date                    TIMESTAMP,
    constraint pk_vehicle_location_priority primary key (vehicle_location_priority_id),
    constraint fk_vehicle_location_priority_vehicle_id foreign key (vehicle_id) references vehicle (vehicle_id),
    constraint fk_vehicle_location_priority_contact_mech_id foreign key (contact_mech_id) references postal_address (contact_mech_id)
);


create table party_driver
(
    party_id           UUID NOT NULL default uuid_generate_v1(),
    status_id          VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_party_driver primary key (party_id),
    constraint fk_party_driver foreign key (party_id) references party (party_id),
    constraint fk_party_driver_status_id foreign key (status_id) references status_item (status_id)
);

create table vehicle_driver
(
    vehicle_driver_id  UUID not null default uuid_generate_v1(),
    party_driver_id    UUID not null,
    vehicle_id         VARCHAR(60),
    from_date          TIMESTAMP,
    thru_date          TIMESTAMP,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_vehicle_driver primary key (vehicle_driver_id),
    constraint fk_vehicle_driver_vehicle_id foreign key (vehicle_id) references vehicle (vehicle_id),
    constraint fk_vehicle_driver_party_driver_id foreign key (party_driver_id) references party_driver (party_id)
);

create table shipment_type
(
    shipment_type_id   VARCHAR(60) NOT NULL,
    parent_type_id     VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_shipment_type primary key (shipment_type_id),
    constraint fk_shipment foreign key (parent_type_id) references shipment_type (shipment_type_id)
);
create table shipment
(
    shipment_id        UUID NOT NULL default uuid_generate_v1(),
    shipment_type_id   VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_shipment primary key (shipment_id),
    constraint fk_shipment_shipment_type_id foreign key (shipment_type_id) references shipment_type (shipment_type_id)
);

create table shipment_item
(
    shipment_item_id              UUID NOT NULL default uuid_generate_v1(),
    shipment_id                   UUID NOT NULL,
    quantity                      Integer,
    pallet                        numeric,
    party_customer_id             UUID,
    ship_to_location_id           UUID,
    order_id                      varchar(60),
    order_item_seq_id             varchar(60),
    facility_id                   varchar(60),
    expected_delivery_date        TIMESTAMP,
    product_transport_category_id VARCHAR(60),
    status_id                     varchar(60),
    scheduled_quantity            int           default 0,
    completed_quantity            int           default 0,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_shipment_item primary key (shipment_item_id),
    constraint fk_shipment_item_shipment_id foreign key (shipment_id) references shipment (shipment_id),
    constraint fk_shipment_item_ship_to_location_id foreign key (ship_to_location_id) references postal_address (contact_mech_id),
    constraint fk_vehicle_type_product_transport_category_id foreign key (product_transport_category_id) references enumeration (enum_id),
    constraint fk_shipment_item_party_customer_id foreign key (party_customer_id) references party_customer (party_id),
    constraint fk_shipment_item_order_id foreign key (order_id) references order_header (order_id),
    CONSTRAINT fk_facility_id FOREIGN KEY (facility_id) REFERENCES facility (facility_id),
    constraint fk_status_item foreign key (status_id) references status_item (status_id)
);

create table shipment_item_status
(
    shipment_item_status_id UUID not null default uuid_generate_v1(),
    shipment_item_id        UUID not null,
    status_id               VARCHAR(60),
    from_date               TIMESTAMP,
    thru_date               TIMESTAMP,
    last_updated_stamp      TIMESTAMP,
    created_stamp           TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_shipment_item_status primary key (shipment_item_status_id),
    constraint fk_shipment_item_status_shipment_item_id foreign key (shipment_item_id) references shipment_item (shipment_item_id),
    constraint fk_shipment_item_status_status_id foreign key (status_id) references status_item (status_id)
);


create table order_shipment
(
    order_shipment_id  UUID NOT NULL default uuid_generate_v1(),
    order_id           VARCHAR(60),
    order_item_seq_id  VARCHAR(60),
    shipment_item_id   UUID,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_order_shipment primary key (order_shipment_id),
    constraint fk_order_shipment_order_item foreign key (order_id, order_item_seq_id) references order_item (order_id, order_item_seq_id),
    constraint fk_order_shipment_shipment_item foreign key (shipment_item_id) references shipment_item (shipment_item_id)
);


create table delivery_plan
(
    delivery_plan_id   UUID NOT NULL default uuid_generate_v1(),
    delivery_date      TIMESTAMP,
    description        TEXT,
    facility_id        VARCHAR(60),
    created_by         VARCHAR(60),
    status_id          VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_delivery_plan primary key (delivery_plan_id),
    constraint fk_delivery_plan_facility_id foreign key (facility_id) references facility (facility_id),
    constraint fk_delivery_plan_created_by foreign key (created_by) references user_login (user_login_id),
    constraint fk_delivery_plan_status_id foreign key (status_id) references status_item (status_id)
);



create table vehicle_delivery_plan
(
    delivery_plan_id   UUID,
    vehicle_id         VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_vehicle_delivery_plan primary key (delivery_plan_id, vehicle_id),
    constraint fk_vehicle_delivery_plan_vehicle_id foreign key (vehicle_id) references vehicle (vehicle_id)
);

create table shipment_item_delivery_plan
(

    delivery_plan_id   UUID,
    shipment_item_id   UUID,

    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_shipment_item_delivery_plan primary key (delivery_plan_id, shipment_item_id),
    constraint fk_shipment_item_delivery_plan_delivery_plan foreign key (delivery_plan_id) references delivery_plan (delivery_plan_id),
    constraint fk_shipment_item_delivery_plan_shipment_item foreign key (shipment_item_id) references shipment_item (shipment_item_id)
);

create table delivery_plan_solution
(
    delivery_plan_id              UUID,
    delivery_plan_solution_seq_id VARCHAR(60),
    status_id                     VARCHAR(60),
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_delivery_plan_solution primary key (delivery_plan_id, delivery_plan_solution_seq_id),
    constraint fk_delivery_plan_solution_status foreign key (status_id) references status_item (status_id),
    constraint fk_delivery_plan_solution_delivery_plan foreign key (delivery_plan_id) references delivery_plan (delivery_plan_id)
);

CREATE TABLE delivery_trip
(
    delivery_trip_id                     uuid        NOT NULL DEFAULT uuid_generate_v1(),
    delivery_plan_id                     uuid        NULL,
    delivery_plan_solution_seq_id        varchar(60) NULL,
    vehicle_id                           varchar(60) NULL,
    driver_id                            uuid        NULL,
    execute_date                         timestamp   NULL,
    distance                             numeric     NULL,
    total_weight                         numeric     NULL,
    total_pallet                         numeric     NULL,
    total_execution_time                 numeric,
    total_location                       int,
    execute_external_vehicle_type_id     varchar(60) NULL,
    status_id                            varchar(60) NULL,
    completed_delivery_trip_detail_count int                  default 0,
    delivery_trip_detail_count           int                  default 0,
    last_updated_stamp                   timestamp   NULL,
    created_stamp                        timestamp   NULL     DEFAULT now(),
    CONSTRAINT pk_delivery_trip PRIMARY KEY (delivery_trip_id),
    CONSTRAINT fk_delivery_trip_driver_id FOREIGN KEY (driver_id) REFERENCES party_driver (party_id),
    CONSTRAINT fk_delivery_trip_delivery_plan_id FOREIGN KEY (delivery_plan_id) REFERENCES delivery_plan (delivery_plan_id),
    CONSTRAINT fk_delivery_trip_external_vehicle_type_id FOREIGN KEY (execute_external_vehicle_type_id) REFERENCES vehicle_type (vehicle_type_id),
    CONSTRAINT fk_delivery_trip_plan_solution FOREIGN KEY (delivery_plan_id, delivery_plan_solution_seq_id) REFERENCES delivery_plan_solution (delivery_plan_id, delivery_plan_solution_seq_id),
    CONSTRAINT fk_delivery_trip_status FOREIGN KEY (status_id) REFERENCES status_item (status_id),
    CONSTRAINT fk_delivery_trip_vehicle_id FOREIGN KEY (vehicle_id) REFERENCES vehicle (vehicle_id)
);

CREATE TABLE delivery_trip_detail
(
    delivery_trip_detail_id uuid        NOT NULL DEFAULT uuid_generate_v1(),
    delivery_trip_id        uuid        NULL,
    sequence_id             int         NULL,
    shipment_item_id        uuid        NULL,
    delivery_quantity       int4        NULL,
    status_id               varchar(60) NULL,
    last_updated_stamp      timestamp   NULL,
    created_stamp           timestamp   NULL     DEFAULT now(),
    CONSTRAINT pk_delivery_trip_detail PRIMARY KEY (delivery_trip_detail_id),
    CONSTRAINT fk_delivery_trip_detail_delivery_trip FOREIGN KEY (delivery_trip_id) REFERENCES delivery_trip (delivery_trip_id),
    CONSTRAINT fk_delivery_trip_detail_shipment FOREIGN KEY (shipment_item_id) REFERENCES shipment_item (shipment_item_id),
    CONSTRAINT fk_delivery_trip_detail_status FOREIGN KEY (status_id) REFERENCES status_item (status_id)
);

create table delivery_trip_detail_status
(
    delivery_trip_detail_status_id UUID      not null default uuid_generate_v1(),
    delivery_trip_detail_id        uuid      NOT NULL,
    status_id                      VARCHAR(60),
    from_date                      TIMESTAMP,
    thru_date                      TIMESTAMP,
    updated_by_user_login_id       VARCHAR(60),
    last_updated_stamp             timestamp NULL,
    created_stamp                  timestamp NULL     DEFAULT now(),

    constraint pk_delivery_trip_detail_status primary key (delivery_trip_detail_status_id),
    constraint fk_delivery_trip_detail_status_delivery_trip_detail_id foreign key (delivery_trip_detail_id) references delivery_trip_detail (delivery_trip_detail_id),
    constraint fk_delivery_trip_detail_status_status_id foreign key (status_id) references status_item (status_id),
    constraint fk_delivery_trip_detail_status_updated_by_user_login_id foreign key (updated_by_user_login_id) references user_login (user_login_id)
);

create table delivery_trip_status
(
    delivery_trip_status_id UUID      not null default uuid_generate_v1(),
    delivery_trip_id        UUID      not null,
    status_id               VARCHAR(60),
    from_date               TIMESTAMP,
    thru_date               TIMESTAMP,
    last_updated_stamp      timestamp NULL,
    created_stamp           timestamp NULL     DEFAULT now(),
    constraint pk_delivery_trip_status_id primary key (delivery_trip_status_id),
    constraint fk_delivery_trip_status_delivery_trip_id foreign key (delivery_trip_id) references delivery_trip (delivery_trip_id),
    constraint fk_delivery_trip_status_status_id foreign key (status_id) references status_item (status_id)
);

CREATE TABLE track_locations
(
    track_location_id  UUID NOT NULL default uuid_generate_v1(),
    party_id           UUID,
    location           VARCHAR(255),
    time_point         TIMESTAMP,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_track_locations PRIMARY KEY (track_location_id),
    CONSTRAINT track_location_party FOREIGN KEY (party_id) REFERENCES party (party_id)
);

CREATE TABLE current_locations
(
    party_id           UUID NOT NULL,
    location           VARCHAR(255),
    time_point         TIMESTAMP,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_current_locations_party_id PRIMARY KEY (party_id)
);

