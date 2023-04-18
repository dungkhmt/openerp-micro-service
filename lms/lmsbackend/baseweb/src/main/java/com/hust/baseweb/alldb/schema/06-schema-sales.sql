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

create table retail_outlet_salesman_vendor
(
    retail_outlet_salesman_vendor_id UUID NOT NULL default uuid_generate_v1(),
    party_retail_outlet_id           UUID NOT NULL,
    party_salesman_id                UUID NOT NULL,
    party_vendor_id                  UUID NOT NULL,
    from_date                        TIMESTAMP,
    thru_date                        TIMESTAMP,
    constraint pk_retail_outlet_salesman_vendor primary key (retail_outlet_salesman_vendor_id),
    constraint fk_retail_outlet_salesman_vendor_customer foreign key (party_retail_outlet_id) references party (party_id),
    constraint fk_retail_outlet_salesman_vendor_salesman foreign key (party_salesman_id) references party (party_id),
    constraint fk_retail_outlet_salesman_vendor_vendor foreign key (party_vendor_id) references party (party_id)
);

create table sales_route_visit_frequency
(
    visit_frequency_id VARCHAR(10),
    description        VARCHAR(100),
    repeat_week        Integer,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_sales_route_frequency primary key (visit_frequency_id)
);

create table sales_route_config
(
    sales_route_config_id UUID NOT NULL default uuid_generate_v1(),
    visit_frequency_id    VARCHAR(10),
    days                  VARCHAR(60),
    status_id             VARCHAR(60),
    description           TEXT,
    last_updated_stamp    TIMESTAMP,
    created_stamp         TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_sales_route_config primary key (sales_route_config_id),
    constraint fk_sales_route_config_retail_outlet_visit_frequency foreign key (visit_frequency_id) references sales_route_visit_frequency (visit_frequency_id),

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

create table sales_route_config_retail_outlet
(
    sales_route_config_retail_outlet_id UUID NOT NULL default uuid_generate_v1(),
    sales_route_planning_period_id      UUID not null,

    visit_frequency_id                  VARCHAR(10),
    sales_route_config_id               UUID,
    retail_outlet_salesman_vendor_id    UUID NOT NULL,
    status_id                           VARCHAR(60),
    start_execute_week                  int,
    start_execute_date                  VARCHAR(60),
    last_updated_stamp                  TIMESTAMP,
    created_stamp                       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_sales_route_config_retail_outlet primary key (sales_route_config_retail_outlet_id),
    constraint fk_sales_route_config_retail_outlet_visit_frequency foreign key (visit_frequency_id) references sales_route_visit_frequency (visit_frequency_id),
    constraint fk_sales_route_config_retail_outlet_sales_route_config_id foreign key (sales_route_config_id) references sales_route_config (sales_route_config_id),
    constraint fk_sales_route_config_retail_outlet_sales_route_planning_period_id foreign key (sales_route_planning_period_id) references sales_route_planning_period (sales_route_planning_period_id),
    constraint fk_sales_route_config_retail_outlet_retail_outlet_salesman_vendor_id foreign key
        (retail_outlet_salesman_vendor_id) references retail_outlet_salesman_vendor (retail_outlet_salesman_vendor_id)
);

create table sales_route_detail
(
    sales_route_detail_id               UUID NOT NULL default uuid_generate_v1(),
    party_salesman_id                   UUID NOT NULL,
    party_retail_outlet_id              UUID NOT NULL,
    party_distributor_id                UUID not null,
    sequence                            Integer,
    execute_date                        VARCHAR(60),
    sales_route_config_retail_outlet_id UUID NOT NULL,
    sales_route_planning_period_id      UUID,
    last_updated_stamp                  TIMESTAMP,
    created_stamp                       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_sales_route_detail primary key (sales_route_detail_id),
    constraint fk_sales_route_detail_sales_route_config_retail_outlet foreign key (sales_route_config_retail_outlet_id)
        references sales_route_config_retail_outlet (sales_route_config_retail_outlet_id),
    constraint fk_sales_route_detail_party_retail_outlet foreign key (party_retail_outlet_id) references party_retail_outlet (party_id),
    constraint fk_sales_route_detail_party_distributor_id foreign key (party_distributor_id) references party_distributor (party_id),
    constraint fk_sales_route_detail_salesman_id foreign key (party_salesman_id) references party_salesman (party_id),
    constraint fk_sales_route_detail_sales_route_planning_period_id foreign key (sales_route_planning_period_id)
        references sales_route_planning_period (sales_route_planning_period_id)
);

create table salesman_checkin_history
(
    salesman_checkin_history_id UUID NOT NULL default uuid_generate_v1(),
    user_login_id               VARCHAR(60),
    party_id                    UUID,
    location                    VARCHAR(60),
    check_in_action             VARCHAR(1),
    time_point                  TIMESTAMP,
    last_updated_stamp          TIMESTAMP,
    created_stamp               TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_salesman_checkin_history primary key (salesman_checkin_history_id),
    constraint fk_salesman_checkin_history_user_login_id foreign key (user_login_id) references user_login (user_login_id),
    constraint fk_salesman_checkin_history_party_id foreign key (party_id) references party (party_id)
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
    party_salesman_id    uuid,

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
    constraint fk_order_header_party_salesman_id foreign key (party_salesman_id) references party (party_id),
    constraint fk_order_header_vendor_id foreign key (vendor_id) references party (party_id),
    constraint fk_party_customer_id foreign key (party_customer_id) references party (party_id)
);

create table order_header_sequence_id
(
    id SERIAL PRIMARY KEY NOT NULL
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

CREATE TABLE product_price
(
    product_price_id         UUID        NOT NULL default uuid_generate_v1(),
    product_id               VARCHAR(60) NOT NULL,
    currency_uom_id          VARCHAR(60),
    from_date                TIMESTAMP,
    thru_date                TIMESTAMP,
    tax_in_price             VARCHAR(1),
    price                    DECIMAL(18, 3),
    created_by_user_login_id VARCHAR(60),
    created_date             TIMESTAMP,
    last_updated_stamp       TIMESTAMP,
    created_stamp            TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_product_price PRIMARY KEY (product_price_id),
    CONSTRAINT fk_product_price_currency_uom_id FOREIGN KEY (currency_uom_id) REFERENCES uom (uom_id),
    CONSTRAINT fk_product_price_created_by_user_login_id FOREIGN KEY (created_by_user_login_id) REFERENCES user_login (user_login_id)
);

-- voucher
create table voucher
(
    voucher_id               uuid      default uuid_generate_v1()
        constraint voucher_pk
            primary key,
    code                     varchar(20)         not null,
    description              varchar(300),
    from_date                timestamp,
    thru_date                timestamp,
    created_date             timestamp default current_timestamp,
    min_order_value          numeric             not null,
    min_discount_amount      numeric             not null,
    max_discount_amount      numeric             not null,
    min_discount_rate        numeric             not null,
    max_discount_rate        numeric             not null,
    usage_limit              integer,
    usage_limit_per_customer integer,
    usage_count              integer   default 0 not null
);

create unique index voucher_code_uindex
    on voucher (code);

create table voucher_rule
(
    voucher_rule_id               uuid      default uuid_generate_v1()
        constraint voucher_rule_pk
            primary key,
    voucher_id                    uuid        not null,
    type                          varchar(20) not null,
    product_id                    varchar(60),
    product_category_id           varchar(60),
    product_transport_category_id varchar(60),
    vendor_code                   varchar(60),
    vendor_category_id            varchar(60),
    customer_code                 varchar(60),
    customer_category_id          varchar(60),
    payment_method                varchar(60),
    created_date                  timestamp default current_timestamp
);



