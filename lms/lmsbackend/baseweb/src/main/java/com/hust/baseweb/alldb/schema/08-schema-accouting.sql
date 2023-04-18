create table invoice_type
(
    invoice_type_id    varchar(60) not null,
    description        varchar(200),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_invoice_type primary key (invoice_type_id)
);
create table invoice_item_type
(
    invoice_item_type_id varchar(60),
    description          varchar(200),
    last_updated_stamp   timestamp,
    created_stamp        timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_invoice_item_type primary key (invoice_item_type_id)
);

create table invoice
(
    invoice_id           varchar(60),
    invoice_type_id      varchar(60),
    status_id            varchar(60),
    invoice_date         TIMESTAMP,
    to_party_customer_id uuid,
    from_vendor_id       uuid,
    amount               decimal(18, 2),
    paid_amount          decimal(18, 2),
    currency_uom_id      varchar(60),
    last_updated_stamp   TIMESTAMP,
    created_stamp        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_invoice primary key (invoice_id),
    constraint fk_invoice_invoice_type_id foreign key (invoice_type_id) references invoice_type (invoice_type_id),
    constraint fk_invoice_to_party_customer_id foreign key (to_party_customer_id) references party (party_id),
    constraint fk_invoice_from_vendor_id foreign key (from_vendor_id) references party (party_id),
    constraint fk_invoice_currency_uom_id foreign key (currency_uom_id) references uom (uom_id),
    constraint fk_invoice_status_id foreign key (status_id) references status_item (status_id)
);


create table invoice_item
(
    invoice_id           varchar(60),
    invoice_item_seq_id  varchar(60),
    invoice_item_type_id varchar(60),
    amount               decimal(18, 2),
    currency_uom_id      varchar(60),
    last_updated_stamp   TIMESTAMP,
    created_stamp        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_invoice_item primary key (invoice_id, invoice_item_seq_id),
    constraint fk_invoice_item_currency_uom_id foreign key (currency_uom_id) references uom (uom_id),
    constraint fk_invoice_item_invoice_item_type foreign key (invoice_item_type_id) references invoice_item_type (invoice_item_type_id)
);

create table order_item_billing
(
    order_id            varchar(60),
    order_item_seq_id   varchar(60),
    invoice_id          varchar(60),
    invoice_item_seq_id varchar(60),
    quantity            int,
    amount              decimal(18, 2),
    currency_uom_id     varchar(60),
    last_updated_stamp  TIMESTAMP,
    created_stamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_order_item_billing primary key (order_id, order_item_seq_id, invoice_id, invoice_item_seq_id),
    constraint fk_order_item_billing_order_id foreign key (order_id, order_item_seq_id) references order_item (order_id, order_item_seq_id),
    constraint fk_invoice_item_currency_uom_id foreign key (currency_uom_id) references uom (uom_id),
    constraint fk_order_item_billing_invoice_id foreign key (invoice_id, invoice_item_seq_id) references invoice_item (invoice_id, invoice_item_seq_id)
);

create table invoice_status
(
    invoice_status_id  uuid not null default uuid_generate_v1(),
    invoice_id         varchar(60),
    status_id          varchar(60),
    from_date          timestamp,
    thru_date          timestamp,
    last_updated_stamp timestamp,
    created_stamp      timestamp     default current_timestamp,
    constraint pk_invoice_status primary key (invoice_status_id),
    constraint fk_invoice_status_id foreign key (status_id) references status_item (status_id),
    constraint fk_invoice_status_invoice_id foreign key (invoice_id) references invoice (invoice_id)
);

create table payment_type
(
    payment_type_id    varchar(60),
    description        varchar(200),
    last_updated_stamp timestamp,
    created_stamp      timestamp default current_timestamp,
    constraint pk_payment_type primary key (payment_type_id)
);

create table payment_method
(
    payment_method_id  varchar(60),
    description        varchar(200),
    last_updated_stamp timestamp,
    created_stamp      timestamp default current_timestamp,
    constraint pk_payment_method primary key (payment_method_id)
);
create table payment
(
    payment_id         varchar(60),
    payment_type_id    varchar(60),
    payment_method_id  varchar(60),
    from_customer_id   uuid,
    to_vendor_id       uuid,
    amount             decimal(18, 2),
    currency_uom_id    varchar(60),
    effective_date     timestamp,
    status_id          varchar(60),
    last_updated_stamp timestamp,
    created_stamp      timestamp default current_timestamp,
    constraint pk_payment primary key (payment_id),
    constraint fk_payment_payment_type_id foreign key (payment_type_id) references payment_type (payment_type_id),
    constraint fk_payment_from_party_id foreign key (from_customer_id) references party (party_id),
    constraint fk_payment_to_party_id foreign key (to_vendor_id) references party (party_id),
    constraint fk_payment_payment_method_id foreign key (payment_method_id) references payment_method (payment_method_id),
    constraint fk_payment_currency_uom_id foreign key (currency_uom_id) references uom (uom_id),
    constraint fk_payment_status_id foreign key (status_id) references status_item (status_id)
);

create table payment_application
(
    payment_application_id uuid not null default uuid_generate_v1(),
    payment_id             varchar(60),
    invoice_id             varchar(60),
    applied_amount         decimal(18, 2),
    currency_uom_id        varchar(60),
    effective_date         timestamp,
    last_updated_stamp     timestamp,
    created_stamp          timestamp     default current_timestamp,
    constraint pk_payment_application primary key (payment_application_id),
    constraint fk_payment_application_payment_id foreign key (payment_id) references payment (payment_id),
    constraint fk_payment_application_currency_uom_id foreign key (currency_uom_id) references uom (uom_id),
    constraint fk_payment_application_invoice_id foreign key (invoice_id) references invoice (invoice_id)
);

create table invoice_sequence_id
(
    id SERIAL PRIMARY KEY NOT NULL
);

create table payment_sequence_id
(
    id SERIAL PRIMARY KEY NOT NULL
);