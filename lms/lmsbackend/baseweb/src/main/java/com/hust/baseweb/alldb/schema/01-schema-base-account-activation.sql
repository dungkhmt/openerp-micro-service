create table account_activation(
    id uuid not null default uuid_generate_v1(),
    user_login_id varchar(60) not null,
    status_id varchar(100),
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_account_activation_id primary key(id)
);
