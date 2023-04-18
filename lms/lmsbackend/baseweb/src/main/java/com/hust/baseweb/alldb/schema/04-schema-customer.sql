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

CREATE TABLE party_retail_outlet
(
    party_id           UUID NOT NULL,
    retail_outlet_code VARCHAR(100),
    retail_outlet_name VARCHAR(200),
    status_id          VARCHAR(60),
    party_type_id      VARCHAR(60),
    description        TEXT,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_party_retail_outlet_party_id PRIMARY KEY (party_id),
    CONSTRAINT fp_party_retail_outlet_party_id FOREIGN KEY (party_id) REFERENCES party (party_id),
    CONSTRAINT fp_party_retail_outlet_party_type_id FOREIGN KEY (party_type_id) REFERENCES party_type (party_type_id),
    CONSTRAINT fp_party_retail_outlet_status_id FOREIGN KEY (status_id) REFERENCES status_item (status_id)
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

