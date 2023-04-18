create table char_group(
    group_id uuid not null default uuid_generate_v1(),
    group_name varchar(500)
    created_by_user_login_id varchar(60),
    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,

    constraint pk_message_id primary key(msg_id)

)
create table user_login_chat_group(
    user_login_id varchar(60),
    group_id uuid,
)
create table message(
    msg_id uuid not null default uuid_generate_v1(),
    message text,
    created_by_user_login_id varchar(60),
    to_user_login_id varchar(60),
    to_group_id uuid,
    reply_to_message_id uuid,
    status_id varchar(200),
    last_updated_stamp         timestamp DEFAULT CURRENT_TIMESTAMP,
    created_stamp              timestamp DEFAULT CURRENT_TIMESTAMP,
    constraint pk_message_id primary key(msg_id)
)
