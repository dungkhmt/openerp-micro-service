create table code_editor_room (
	id uuid not null default uuid_generate_v1(),
	room_name varchar(2000) not null ,
	room_master_id varchar(255) not null,
	create_date timestamp default current_timestamp,
	edit_date timestamp default current_timestamp,
	isPublic bool not null default false,
	access_permission varchar(255) default 'VIEWER',
	constraint pk_code_editor_room primary key (id)
);

create table code_editor_source(
	id uuid not null default uuid_generate_v1(),
	language varchar(255) not null,
	source text not null,
	room_id uuid not null,
	create_date timestamp default current_timestamp,
	edit_date timestamp default current_timestamp,
	edit_by varchar(255),
	constraint pk_code_editor_source primary key (id),
	constraint fk_code_editor_source__code_editor_room_id foreign key (room_id) references code_editor_room (id)
)

create table code_editor_shared_user_room(
	room_id uuid not null,
	user_id varchar(255) not null,
	constraint pk_code_editor_shared_user_room primary key (room_id, user_id),
	constraint fk_code_editor_shared_user_room__room_id foreign key (room_id) references code_editor_room (id),
	constraint fk_code_editor_shared_user_room__user_id foreign key (user_id) references user_login (user_login_id)
)