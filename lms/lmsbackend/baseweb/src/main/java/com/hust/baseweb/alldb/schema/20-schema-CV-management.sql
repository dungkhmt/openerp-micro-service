create table cv(
    cv_id uuid not null uuid_generate_v1(),
    user_login_id varcar(60),
    last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	constraint pk_cv primary key (cv_id)

);
create table cv_item_type(
    cv_item_type_id uuid not null uuid_generate_v1(),
    cv_item_name varchar(500),
    constraint pk_cv_item_type primary key (cv_item_type_id)
);
create table cv_item(
    cv_item_id uuid not null uuid_generate_v1(),
    cv_item_type_id uuid,
    cv_id uuid,
    from_date timestamp,
    thru_date                      timestamp,
    content text,
    constraint pk_cv_id primary key (cv_item_id)
);
