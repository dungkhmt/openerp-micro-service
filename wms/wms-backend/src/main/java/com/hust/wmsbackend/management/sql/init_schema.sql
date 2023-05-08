-- show all tables
select *
from pg_catalog.pg_tables;

-- describe table
select *
from information_schema.columns
where table_name = 'inventory_item';

-- install uuid generate function
create
    extension if not exists "uuid-ossp";
-- SELECT * FROM pg_extension;
select uuid_generate_v1();
---
create table inventory_item
(
    inventory_item_id      uuid           not null default uuid_generate_v1() primary key,
    product_id             uuid           not null,
    lot_id                 varchar(60)    not null,
    warehouse_id            uuid           not null,
    bay_id                 uuid           NOT NULL,
    quantity_on_hand_total decimal(18, 2) not null, -- Do hàng hóa có thể tính theo cân nặng nên quantity có data type là decimal
    import_price           decimal(18, 2) not null,
    currency_uom_id        varchar(60)    not null default 'VND', -- TODO: check lai voi database hien tai cua team
    datetime_received      timestamp      not null default current_timestamp,
    expire_date            timestamp,
    last_updated_stamp     timestamp,
    created_stamp          timestamp               default CURRENT_TIMESTAMP,
    description            varchar(100),
    is_init_quantity bool default false
--     status_id                  VARCHAR(60),
--     datetime_manufactured      TIMESTAMP,
--     activation_valid_thru      TIMESTAMP,
--     unit_cost                  DECIMAL(18, 6),
--     available_to_promise_total DECIMAL(18, 6),
);

create table inventory_item_detail
(
    inventory_item_detail_id uuid           not null default uuid_generate_v1() primary key,
    inventory_item_id        uuid           not null,
    quantity_on_hand_diff    decimal(18, 2) not null,
    effective_date           timestamp      not null default current_timestamp
);

create table warehouse
(
    warehouse_id uuid         not null default uuid_generate_v1() primary key,
    name        varchar(100) not null,
    code        varchar(100),
    width       int,
    length      int,
    address     varchar(100),
    longitude  decimal(20, 14),
    latitude   decimal(20, 14)
);

create table product_category
(
    category_id uuid        not null default uuid_generate_v1() primary key,
    name        varchar(60) not null
);

create table product
(
    product_id         uuid         not null default uuid_generate_v1() primary key,
    code               varchar(60)  not null,
    name               varchar(100) not null,
    description        varchar(100),

    height             decimal(18, 2),
    weight             decimal(18, 2),
    area               decimal(18, 2),

    uom                varchar(20),
    category_id        uuid,

    image_content_type varchar(20),
    image_size         int,
    image_data         oid
);

create table bay
(
    bay_id      uuid        not null default uuid_generate_v1() primary key,
    warehouse_id uuid        not null,
    code        varchar(60) not null,
    x           int         not null,
    y           int         not null,
    x_long       int         not null,
    y_long       int         not null
);

create table product_bay
(
    product_bay_id uuid not null default uuid_generate_v1() primary key,
    product_id     uuid not null,
    bay_id         uuid not null,
    quantity       decimal(18, 2)
);

create table product_warehouse
(
    product_warehouse_id uuid not null default uuid_generate_v1() primary key,
    product_id          uuid not null,
    warehouse_id         uuid not null,
    quantity_on_hand    decimal(18, 2)
);

create table receipt
(
    receipt_id         uuid not null default uuid_generate_v1() primary key,
    receipt_date       timestamp,
    warehouse_id       uuid,
    receipt_name       varchar(60),
    description        varchar(200),
    last_updated_stamp TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    created_reason varchar(255),
    expected_receipt_date timestamp,
    status varchar(100),
    created_by varchar(255),
    approved_by varchar(255),
    cancelled_by varchar(255)
);

create table receipt_item_request
(
    receipt_item_request_id uuid not null default uuid_generate_v1() primary key,
    receipt_id uuid not null ,
    product_id uuid not null ,
    quantity decimal(18, 2) not null ,
    warehouse_id uuid
)

create table receipt_item
(
    receipt_item_id    uuid not null default uuid_generate_v1() primary key,
    receipt_id         uuid,
    product_id         uuid,
    quantity           DECIMAL(18, 6),
    bay_id             uuid,
    lot_id             varchar(60),
    import_price       decimal(18, 6),
    expired_date       TIMESTAMP,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    receipt_item_request_id uuid not null
);

create table product_price
(
    product_price_id uuid not null default uuid_generate_v1() primary key,
    product_id       uuid not null,
    price            decimal(18, 2),
    start_date       timestamp     default current_timestamp,
    end_date         timestamp,
    description      varchar(200)
);

create table account_activation
(
    id uuid primary key not null default uuid_generate_v1(),
    user_login_id varchar(60) not null ,
    status_id varchar(100),
    last_updated_stamp timestamp ,
    created_stamp timestamp default current_timestamp
);

create table customer_address
(
    customer_address_id uuid not null primary key default uuid_generate_v1(),
    user_login_id varchar(255) not null ,
    address_name varchar(255),
    longitude  decimal(20, 14),
    latitude   decimal(20, 14)
);

create table sale_order_header
(
    order_id uuid primary key not null default uuid_generate_v1(),
    user_login_id varchar(255),
    order_date timestamp default current_timestamp,
    delivery_fee decimal(18, 2),
    total_product_cost decimal(18, 2),
    total_order_cost decimal(18, 2),
    customer_address_id uuid not null,
    customer_name varchar(255),
    customer_phone_number varchar(255),
    description varchar(255),
    payment_type varchar(50),
    order_type varchar(50),
    last_updated_stamp timestamp default current_timestamp,
    created_stamp timestamp default current_timestamp,
    status varchar(100),
    approved_by varchar(50),
    cancelled_by varchar(50)
);

create table sale_order_item
(
    sale_order_item_id uuid primary key not null default uuid_generate_v1(),
    order_id uuid not null,
    product_id uuid not null ,
    quantity int,
    price_unit decimal(18, 2)
);

create table delivery_person
(
    full_name varchar(100) not null,
    phone_number varchar(50),
    user_login_id varchar(50) primary key not null
);

create table assigned_order_item
(
    assigned_order_item_id uuid primary key not null default uuid_generate_v1(),
    order_id uuid not null,
    product_id uuid not null ,
    quantity decimal(18, 2) not null ,
    bay_id uuid not null ,
    warehouse_id uuid not null ,
    assigned_by varchar(50),
    last_updated_stamp timestamp default current_timestamp,
    created_stamp timestamp default current_timestamp,
    lot_id varchar(50),
    status varchar(50) not null default 'CREATED',
    inventory_item_id uuid
);

create table shipment
(
    shipment_id varchar(50) primary key not null,
    expected_delivery_stamp timestamp,
    created_stamp timestamp default current_timestamp,
    last_updated_stamp timestamp default current_timestamp,
    created_by varchar(50),
    is_deleted boolean default false
);

create table delivery_trip
(
    delivery_trip_id varchar(50) primary key not null,
    vehicle_id uuid,
    delivery_person_id varchar(50),
    distance decimal(18,2),
    total_weight decimal(18,2),
    total_locations int,
    last_updated_stamp timestamp default current_timestamp,
    created_stamp timestamp default current_timestamp,
    created_by varchar(50),
    is_deleted boolean default false,
    shipment_id varchar(50),
    warehouse_id uuid,
    status varchar(50)
);

create table delivery_trip_item
(
    delivery_trip_item_id varchar(50) primary key not null,
    delivery_trip_id varchar(50),
    sequence int,
    order_id uuid,
    assigned_order_item_id uuid,
    quantity decimal(18, 2),
    is_deleted boolean default false,
    status varchar(50)
);

create table delivery_trip_path
(
    delivery_trip_path_id int primary key not null,
    delivery_trip_id varchar(50) not null,
    longitude  decimal(20, 14),
    latitude   decimal(20, 14),
    created_stamp timestamp default current_timestamp
);

create table entity_authorization
(
    id varchar(200) not null primary key,
    role_id varchar(50),
    description varchar(200),
    last_updated timestamp default current_timestamp,
    created timestamp default current_timestamp
);

alter table bay
    add constraint fk_bay_warehouse_id foreign key (warehouse_id) references warehouse (warehouse_id);

-- alter table wmsv2_warehouse rename column facility to code;
-- TODO: Add constraint for tables
alter table bay
add constraint fk_bay_warehouse_on_delete_cascade
foreign key (warehouse_id)
references warehouse (warehouse_id)
on delete cascade;

alter table product_warehouse
add constraint fk_product_warehouse_product_on_delete_cascade
foreign key (product_id)
references product (product_id)
on delete cascade;

alter table inventory_item
add constraint fk_inventory_item_product_on_delete_cascade
foreign key (product_id)
references product (product_id)
on delete cascade;

alter table inventory_item_detail
add constraint fk_inventory_item_detail_inventory_item_on_delete_cascade
foreign key (inventory_item_id)
references inventory_item (inventory_item_id)
on delete cascade;

alter table receipt
add constraint fk_receipt_warehouse_on_delete_cascade
foreign key (warehouse_id)
references warehouse (warehouse_id)
on delete cascade;

alter table receipt_item
add constraint fk_receipt_receipt_item_on_delete_cascade
foreign key (receipt_id)
references receipt (receipt_id)
on delete cascade;

alter table receipt_item
add constraint fk_receipt_item_product_on_delete_cascade
foreign key (product_id)
references product (product_id)
on delete cascade;

alter table product_price
add constraint fk_product_price_product_on_delete_cascade
foreign key (product_id)
references product (product_id)
on delete cascade;

alter table sale_order_item
add constraint fk_sale_order_item_sale_order_header
foreign key (order_id)
references sale_order_header(order_id)
on delete cascade;

alter table receipt_item_request
add constraint fk_receipt_item_request_receipt
foreign key (receipt_id)
references receipt (receipt_id)
on delete cascade;
