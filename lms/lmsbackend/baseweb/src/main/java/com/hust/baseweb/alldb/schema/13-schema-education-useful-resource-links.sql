create table edu_resource_domain(
    id uuid not null default uuid_generate_v1(),
    name varchar(500),
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint pk_edu_recourse_domain primary key(id)
);

create table edu_resource(
    id uuid not null default uuid_generate_v1(),
    link varchar(500),
    domain_id uuid,
    created_by_user_login_id varchar(60),
    description text,
    status_id varchar(200),
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint pk_edu_resource primary key(id),
    constraint fk_edu_resource_domain foreign key(domain_id) references  edu_resource_domain(id),
    constraint fk_edu_resource_created_by_user_login_id foreign key(created_by_user_login_id) references user_login(user_login_id)
);
