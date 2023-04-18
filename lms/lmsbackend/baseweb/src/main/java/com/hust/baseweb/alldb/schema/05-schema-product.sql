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
    primary_img                   uuid,
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
    constraint fk_vehicle_type_product_transport_category_id foreign key (product_transport_category_id) references enumeration (enum_id),
    constraint fk_product_avatar_content foreign key (primary_img) references content (content_id)
);



