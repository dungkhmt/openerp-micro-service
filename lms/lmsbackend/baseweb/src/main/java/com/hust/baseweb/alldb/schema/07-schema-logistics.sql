create table receipt
(
    receipt_id         varchar(60),
    receipt_date       timestamp,
    facility_id        varchar(60),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_receipt primary key (receipt_id),
    constraint fk_receipt_facility_id foreign key (facility_id) references facility (facility_id)
);

create table receipt_sequence_id
(
    id SERIAL PRIMARY KEY NOT NULL
);

create table receipt_item
(
    receipt_item_id    uuid not null default uuid_generate_v1(),
    receipt_id         varchar(60),
    product_id         varchar(60),
    quantity           int,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_receipt_item primary key (receipt_item_id),
    constraint fk_receipt_item_product_id foreign key (product_id) references product (product_id),
    constraint fk_receipt_item_receipt_id foreign key (receipt_id) references receipt (receipt_id)
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
    processed_by_user_login_id    varchar(60),
    scheduled_quantity            int           default 0,
    completed_quantity            int           default 0,
    last_updated_stamp            TIMESTAMP,
    created_stamp                 TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_shipment_item primary key (shipment_item_id),
    constraint fk_shipment_item_shipment_id foreign key (shipment_id) references shipment (shipment_id),
    constraint fk_shipment_item_ship_to_location_id foreign key (ship_to_location_id) references postal_address (contact_mech_id),
    constraint fk_vehicle_type_product_transport_category_id foreign key (product_transport_category_id) references enumeration (enum_id),
    constraint fk_shipment_item_party_customer_id foreign key (party_customer_id) references party (party_id),
    constraint fk_shipment_item_order_id foreign key (order_id) references order_header (order_id),
    CONSTRAINT fk_facility_id FOREIGN KEY (facility_id) REFERENCES facility (facility_id),
    constraint fk_shipment_item_processed_by_user_login_id foreign key (processed_by_user_login_id) references user_login (user_login_id),
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


create table shipment_item_role
(
    shipment_item_role_id UUID not null default uuid_generate_v1(),
    shipment_item_id      UUID not null,
    party_id              uuid not null,
    role_type_id          VARCHAR(60),
    from_date             TIMESTAMP,
    thru_date             TIMESTAMP,

    last_updated_stamp    TIMESTAMP,
    created_stamp         TIMESTAMP     default CURRENT_TIMESTAMP,
    constraint pk_shipment_item_role primary key (shipment_item_role_id),
    constraint fk_shipment_item_role_role_type_id foreign key (role_type_id) references role_type (role_type_id),
    constraint fk_shipment_item_role_shipment_item_id foreign key (shipment_item_id) references shipment_item (shipment_item_id),
    constraint fk_shipment_item_role_party_id foreign key (party_id) references party (party_id)

);
