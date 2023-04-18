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
    maxDistancePerTrip            int,
    average_speed                 int,
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

create table delivery_plan
(
    delivery_plan_id            varchar(60) NOT NULL,
    delivery_date               TIMESTAMP,
    description                 TEXT,
    facility_id                 VARCHAR(60),
    created_by                  VARCHAR(60),
    status_id                   VARCHAR(60),
    total_weight_shipment_items numeric,
    last_updated_stamp          TIMESTAMP,
    created_stamp               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_delivery_plan primary key (delivery_plan_id),
    constraint fk_delivery_plan_facility_id foreign key (facility_id) references facility (facility_id),
    constraint fk_delivery_plan_created_by foreign key (created_by) references user_login (user_login_id),
    constraint fk_delivery_plan_status_id foreign key (status_id) references status_item (status_id)
);

create table delivery_plan_sequence_id
(
    id SERIAL PRIMARY KEY NOT NULL
);

create table vehicle_delivery_plan
(
    delivery_plan_id   varchar(60),
    vehicle_id         VARCHAR(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_vehicle_delivery_plan primary key (delivery_plan_id, vehicle_id),
    constraint fk_vehicle_delivery_plan_delivery_plan_id foreign key (delivery_plan_id) references delivery_plan (delivery_plan_id),
    constraint fk_vehicle_delivery_plan_vehicle_id foreign key (vehicle_id) references vehicle (vehicle_id)
);

create table shipment_item_delivery_plan
(

    delivery_plan_id   varchar(60),
    shipment_item_id   uuid,

    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_shipment_item_delivery_plan primary key (delivery_plan_id, shipment_item_id),
    constraint fk_shipment_item_delivery_plan_delivery_plan foreign key (delivery_plan_id) references delivery_plan (delivery_plan_id),
    constraint fk_shipment_item_delivery_plan_shipment_item foreign key (shipment_item_id) references shipment_item (shipment_item_id)
);

create table delivery_plan_solution
(
    delivery_plan_id              varchar(60),
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
    delivery_trip_id                     varchar(60) NOT NULL,
    delivery_plan_id                     varchar(60) NULL,
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
    completed_delivery_trip_detail_count int              default 0,
    delivery_trip_detail_count           int              default 0,
    last_updated_stamp                   timestamp   NULL,
    created_stamp                        timestamp   NULL DEFAULT now(),
    CONSTRAINT pk_delivery_trip PRIMARY KEY (delivery_trip_id),
    CONSTRAINT fk_delivery_trip_driver_id FOREIGN KEY (driver_id) REFERENCES party_driver (party_id),
    CONSTRAINT fk_delivery_trip_delivery_plan_id FOREIGN KEY (delivery_plan_id) REFERENCES delivery_plan (delivery_plan_id),
    CONSTRAINT fk_delivery_trip_external_vehicle_type_id FOREIGN KEY (execute_external_vehicle_type_id) REFERENCES vehicle_type (vehicle_type_id),
    CONSTRAINT fk_delivery_trip_plan_solution FOREIGN KEY (delivery_plan_id, delivery_plan_solution_seq_id) REFERENCES delivery_plan_solution (delivery_plan_id, delivery_plan_solution_seq_id),
    CONSTRAINT fk_delivery_trip_status FOREIGN KEY (status_id) REFERENCES status_item (status_id),
    CONSTRAINT fk_delivery_trip_vehicle_id FOREIGN KEY (vehicle_id) REFERENCES vehicle (vehicle_id)
);

create table delivery_trip_sequence_id
(
    id SERIAL PRIMARY KEY NOT NULL
);

CREATE TABLE delivery_trip_detail
(
    delivery_trip_detail_id varchar(60) NOT NULL,
    delivery_trip_id        varchar(60) NULL,
    sequence_id             int         NULL,
    shipment_item_id        uuid        NULL,
    delivery_quantity       int4        NULL,
    status_id               varchar(60) NULL,
    last_updated_stamp      timestamp   NULL,
    created_stamp           timestamp   NULL DEFAULT now(),
    CONSTRAINT pk_delivery_trip_detail PRIMARY KEY (delivery_trip_detail_id),
    CONSTRAINT fk_delivery_trip_detail_delivery_trip FOREIGN KEY (delivery_trip_id) REFERENCES delivery_trip (delivery_trip_id),
    CONSTRAINT fk_delivery_trip_detail_shipment FOREIGN KEY (shipment_item_id) REFERENCES shipment_item (shipment_item_id),
    CONSTRAINT fk_delivery_trip_detail_status FOREIGN KEY (status_id) REFERENCES status_item (status_id)
);

create table delivery_trip_detail_sequence_id
(
    id SERIAL PRIMARY KEY NOT NULL
);

create table delivery_trip_detail_status
(
    delivery_trip_detail_status_id UUID        not null default uuid_generate_v1(),
    delivery_trip_detail_id        varchar(60) NOT NULL,
    status_id                      VARCHAR(60),
    from_date                      TIMESTAMP,
    thru_date                      TIMESTAMP,
    updated_by_user_login_id       VARCHAR(60),
    last_updated_stamp             timestamp   NULL,
    created_stamp                  timestamp   NULL     DEFAULT now(),

    constraint pk_delivery_trip_detail_status primary key (delivery_trip_detail_status_id),
    constraint fk_delivery_trip_detail_status_delivery_trip_detail_id foreign key (delivery_trip_detail_id) references delivery_trip_detail (delivery_trip_detail_id),
    constraint fk_delivery_trip_detail_status_status_id foreign key (status_id) references status_item (status_id),
    constraint fk_delivery_trip_detail_status_updated_by_user_login_id foreign key (updated_by_user_login_id) references user_login (user_login_id)
);

create table delivery_trip_status
(
    delivery_trip_status_id UUID        not null default uuid_generate_v1(),
    delivery_trip_id        varchar(60) not null,
    status_id               VARCHAR(60),
    from_date               TIMESTAMP,
    thru_date               TIMESTAMP,
    last_updated_stamp      timestamp   NULL,
    created_stamp           timestamp   NULL     DEFAULT now(),
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

