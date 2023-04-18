create table data_quality_check_rule(
    rule_id varchar(100),
    rule_description varchar(500),
    params varchar(2000),
    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,

    constraint pk_data_quality_check_rule primary key(rule_id)
);

create table data_quality_check_master(
    id uuid not null default uuid_generate_v1(),
    rule_id varchar(100),
    created_by_user_login_id varchar(60),
    meta_data text,
    table_name varchar(100),
    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_data_quality_check primary key (table_id),
    constraint fk_data_quality_check_rule foreign key(rule_id) references  data_quality_check_rule(rule_id),
    constraint fk_data_quality_check_created_by_user foreign key(created_by_user_login_id) references user_login(user_login_id)
);

create table data_quality_check_result(
    id uuid not null default uuid_generate_v1(),
    data_quality_check_master_id uuid not null,
    instance varchar(200),
    result varchar(500),
    status_id varchar(100),
    message text,

    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_data_quality_check_result primary key (id),
    constraint fk_data_quality_check_result_master_id foreign key (data_quality_check_master_id) references data_quality_check_master(id)
);
