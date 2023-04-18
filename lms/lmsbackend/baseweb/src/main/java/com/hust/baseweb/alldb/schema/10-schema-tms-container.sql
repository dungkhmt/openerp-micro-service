create table cont_container_type
(
    container_type_id  VARCHAR(60),
    description        VARCHAR(200),
    unit               int,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_cont_container_type primary key (container_type_id)
);

create table cont_container
(
    container_id       VARCHAR(60),
    container_type_id  VARCHAR(60),

    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_cont_container primary key (container_id),
    constraint fk_cont_container foreign key (container_type_id) references cont_container_type (container_type_id)
);
create table cont_trailer
(
    trailer_id         VARCHAR(60),
    description        VARCHAR(200),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_trailer primary key (trailer_id)
);

create table cont_port
(
    port_id            VARCHAR(60),
    port_name          VARCHAR(60),

    contact_mech_id    UUID not null,

    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_cont_port primary key (port_id),
    constraint fk_cont_port_contact_mech_id foreign key (contact_mech_id) references postal_address (contact_mech_id)
);

create table cont_depot_truck
(
    depot_truck_id     VARCHAR(60),
    depot_truck_name   VARCHAR(200),
    contact_mech_id    UUID not null,

    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_cont_depot_truck primary key (depot_truck_id),
    constraint fk_cont_depot_truck_contact_mech foreign key (contact_mech_id) references postal_address (contact_mech_id)
);

create table cont_depot_trailer
(
    depot_trailer_id   VARCHAR(60),
    depot_trailer_name VARCHAR(200),
    contact_mech_id    UUID not null,

    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_cont_depot_trailer primary key (depot_trailer_id),
    constraint fk_cont_depot_trailer_contact_mech foreign key (contact_mech_id) references postal_address (contact_mech_id)

);
create table cont_depot_container
(
    depot_container_id   VARCHAR(60),
    depot_container_name VARCHAR(200),
    contact_mech_id      UUID not null,

    last_updated_stamp   TIMESTAMP,
    created_stamp        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_cont_depot_container primary key (depot_container_id),
    constraint fk_cont_depot_container_contact_mech foreign key (contact_mech_id) references postal_address (contact_mech_id)

);

create table cont_request_import_empty
(
    request_import_empty_id  UUID not null default uuid_generate_v1(),
    facility_id              VARCHAR(60),
    container_type_id        VARCHAR(60),
    number_containers        int,
    has_trailer              VARCHAR(1),

    request_date_time        TIMESTAMP,
    early_date_time_expected TIMESTAMP,
    late_date_time_expected  TIMESTAMP,

    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    constraint pk_cont_request_import_empty primary key (request_import_empty_id),
    constraint fk_cont_request_import_empty_facility foreign key (facility_id) references facility (facility_id),
    constraint fk_cont_request_import_empty_container_type foreign key (container_type_id) references cont_container_type (container_type_id)
);

create table cont_request_import_full
(
    request_import_full_id   UUID not null default uuid_generate_v1(),
    facility_id              VARCHAR(60),
    port_id                  VARCHAR(60),
    container_type_id        VARCHAR(60),
    number_containers        int,
    leave_trailer            VARCHAR(1),

    request_date_time        TIMESTAMP,
    early_date_time_expected TIMESTAMP,
    late_date_time_expected  TIMESTAMP,

    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_cont_request_import_full primary key (request_import_full_id),
    constraint fk_cont_request_import_full_facility foreign key (facility_id) references facility (facility_id),
    constraint fk_cont_request_import_full_port foreign key (port_id) references cont_port (port_id),
    constraint fk_cont_request_import_full_container_type foreign key (container_type_id) references cont_container_type (container_type_id)
);

create table cont_request_export_empty
(
    request_export_empty_id  UUID not null default uuid_generate_v1(),
    facility_id              VARCHAR(60),
    container_type_id        VARCHAR(60),
    number_containers        int,
    leave_trailer            VARCHAR(1),

    request_date_time        TIMESTAMP,
    early_date_time_expected TIMESTAMP,
    late_date_time_expected  TIMESTAMP,

    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    constraint pk_cont_request_export_empty primary key (request_export_empty_id),
    constraint fk_cont_request_export_empty_facility foreign key (facility_id) references facility (facility_id),
    constraint fk_cont_request_export_empty_container_type foreign key (container_type_id) references cont_container_type (container_type_id)
);

create table cont_request_export_full
(
    request_export_full_id   UUID not null default uuid_generate_v1(),
    facility_id              VARCHAR(60),
    port_id                  VARCHAR(60),
    container_type_id        VARCHAR(60),
    number_containers        int,
    has_trailer              VARCHAR(1),

    request_date_time        TIMESTAMP,
    early_date_time_expected TIMESTAMP,
    late_date_time_expected  TIMESTAMP,

    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_cont_request_export_full primary key (request_export_full_id),
    constraint fk_cont_request_export_full_facility foreign key (facility_id) references facility (facility_id),
    constraint fk_cont_request_export_full_port foreign key (port_id) references cont_port (port_id),
    constraint fk_cont_request_export_full_container_type foreign key (container_type_id) references cont_container_type (container_type_id)
);

create table cont_request_between_warehouse
(
    request_between_warehouse_id           UUID not null default uuid_generate_v1(),

    container_type_id                      VARCHAR(60),
    number_containers                      int,

    from_facility_id                       VARCHAR(60),
    early_date_time_expected_from_facility TIMESTAMP,
    late_date_time_expected_from_facility  TIMESTAMP,
    has_trailer                            VARCHAR(1),

    to_facility_id                         VARCHAR(60),
    early_date_time_expected_to_facility   TIMESTAMP,
    late_date_time_expected_to_facility    TIMESTAMP,
    leave_trailer                          VARCHAR(1),

    request_date_time                      TIMESTAMP,

    last_updated_stamp                     TIMESTAMP,
    created_stamp                          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_cont_request_between_warehouse primary key (request_between_warehouse_id),
    constraint fk_cont_request_between_warehouse_from_facility foreign key (from_facility_id) references facility (facility_id),
    constraint fk_cont_request_between_warehouse_to_facility foreign key (to_facility_id) references facility (facility_id),
    constraint fk_cont_request_between_warehouse_container_type foreign key (container_type_id) references cont_container_type (container_type_id)
);

alter table cont_request_import_full
    add party_customer_id uuid;
alter table cont_request_import_full
    add CONSTRAINT fk_cont_request_import_full_party_customer FOREIGN KEY (party_customer_id) REFERENCES party_customer (party_id);
alter table cont_container
    add column container_name varchar(60);

alter table cont_request_import_empty
    add party_customer_id uuid;
alter table cont_request_import_empty
    add CONSTRAINT fk_cont_request_import_empty_party_customer FOREIGN KEY (party_customer_id) REFERENCES party_customer (party_id);

alter table cont_request_export_full
    add party_customer_id uuid;
alter table cont_request_export_full
    add CONSTRAINT fk_cont_request_export_full_party_customer FOREIGN KEY (party_customer_id) REFERENCES party_customer (party_id);

alter table cont_request_export_empty
    add party_customer_id uuid;
alter table cont_request_export_empty
    add CONSTRAINT fk_cont_request_export_empty_party_customer FOREIGN KEY (party_customer_id) REFERENCES party_customer (party_id);
