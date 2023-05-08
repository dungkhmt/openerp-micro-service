create table passbook_passbook(
    pass_book_id uuid not null default uuid_generate_v1(),
    pass_book_name varchar(200),
    amount_money_deposit int,
    user_id varchar(60),
    rate numeric,
    duration int,
    status_id varchar(100),
    created_by_user_id varchar(60),
    created_date timestamp,
    end_date timestamp,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_pass_book_primary_key primary key (pass_book_id),
    constraint fk_pass_book_user_id foreign key(user_id) references user_login(user_login_id),
    constraint fk_pass_book_created_by_user_id foreign key(created_by_user_id) references user_login(user_login_id)
);

create table passbook_rate_config(
    rate_config_id uuid not null default uuid_generate_v1(),
    current_pass_book_rate numeric,

    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
	constraint pk_passbook_rate_config primary key(rate_config_id)

);