-- Drop table

-- DROP TABLE public.notification_type;

create TABLE public.notification_type (
	notification_type_id varchar(100) NOT NULL,
	notification_type_name varchar(200) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT pk_notification_type_id PRIMARY KEY (notification_type_id)
);

-- Drop table

-- DROP TABLE public.notifications;

create TABLE public.notifications (
	id uuid NOT NULL DEFAULT uuid_generate_v1(),
	"content" varchar(500) NULL,
	notification_type_id varchar(100) NULL,
	from_user varchar(60) NULL,
	to_user varchar(60) NULL,
	url varchar(200) NULL,
	status_id varchar(60) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT pk_notification_id PRIMARY KEY (id),
	CONSTRAINT fk_notification_notification_type_id FOREIGN KEY (notification_type_id) REFERENCES notification_type(notification_type_id),
	CONSTRAINT fk_notification_user_login_id FOREIGN KEY (to_user) REFERENCES user_login(user_login_id)
);

