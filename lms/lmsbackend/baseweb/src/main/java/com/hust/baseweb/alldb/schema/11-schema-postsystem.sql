drop table post_office_fixed_trip cascade;
drop table post_office_fixed_trip_execute cascade;
drop table post_ship_order_fixed_trip_post_office_assignment cascade;
drop table post_ship_order_postman_last_mile_assignment cascade;
drop table post_ship_order_trip_post_office_assignment cascade;

drop table post_ship_order cascade;
drop table post_office_trip cascade;
drop table post_driver cascade;
drop table post_driver_post_office_assignment cascade;
drop table post_package_type cascade;
drop table post_customer cascade;
drop table post_office cascade;
drop table post_office_relationship cascade;
drop table postman cascade;


create table post_package_type
(
    post_package_type_id   VARCHAR(60),
    post_package_type_name VARCHAR(200),
    last_updated_stamp     TIMESTAMP,
    created_stamp          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_post_package_type primary key (post_package_type_id)
);

create table post_customer
(
    post_customer_id   UUID      default uuid_generate_v1(),
    post_customer_name VARCHAR(200),
    contact_mech_id    UUID,
    phone_num          VARCHAR(20),
    party_id           UUID,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_post_customer primary key (post_customer_id),
    constraint fk_post_customer_party_id foreign key (party_id) references party (party_id),
    constraint fk_post_customer_contact_mech_id foreign key (contact_mech_id) references postal_address (contact_mech_id)
);

create table post_office
(
    post_office_id     VARCHAR(60),
    post_office_name   VARCHAR(200),
    contact_mech_id    UUID not null,
    post_office_level  int,
    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_post_office_id primary key (post_office_id),
    constraint fk_post_office_contact_mech_id foreign key (contact_mech_id) references postal_address(contact_mech_id)
);

create table post_office_relationship
(
    post_office_relationship_id UUID not null default uuid_generate_v1(),
    post_office_id              VARCHAR(60),
    parent_post_office_id       VARCHAR(60),
    from_date                   TIMESTAMP,
    thru_date                   TIMESTAMP,

    last_updated_stamp          TIMESTAMP,
    created_stamp               TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    constraint pk_post_office_relationship_id primary key (post_office_relationship_id),
    constraint fk_post_office_relationship_post_office_id foreign key (post_office_id) references post_office (post_office_id),
    constraint fk_post_office_relationship_parent_post_office_id foreign key (parent_post_office_id) references post_office (post_office_id)
);

create table postman
(
    postman_id         UUID,
    postman_name       VARCHAR(200),
    post_office_id     VARCHAR(60),

    last_updated_stamp TIMESTAMP,
    created_stamp      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint pk_postman_id primary key (postman_id),
    constraint fk_postman_id foreign key (postman_id) references party (party_id),
    constraint fk_postman_post_office_id foreign key (post_office_id) references post_office (post_office_id)
);

CREATE TABLE public.post_office_trip (
	post_office_trip_id uuid NOT NULL DEFAULT uuid_generate_v1(),
	from_post_office_id varchar(60) NULL,
	to_post_office_id varchar(60) NULL,
	from_date timestamp NULL,
	thru_date timestamp NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT now(),
	CONSTRAINT pk_post_office_trip_id PRIMARY KEY (post_office_trip_id),
	CONSTRAINT fk_post_office_trip_id_from_post_office_id FOREIGN KEY (from_post_office_id) REFERENCES post_office(post_office_id),
	CONSTRAINT fk_post_office_trip_id_to_post_office_id FOREIGN KEY (to_post_office_id) REFERENCES post_office(post_office_id)
);

CREATE TABLE public.post_office_fixed_trip (
	post_office_fixed_trip_id uuid NOT NULL DEFAULT uuid_generate_v1(),
	schedule_departure_time varchar(20) NULL,
	from_date timestamp NULL,
	thru_date timestamp NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT now(),
	post_office_trip_id uuid NULL,
	CONSTRAINT pk_post_office_fixed_trip_id PRIMARY KEY (post_office_fixed_trip_id),
	CONSTRAINT fk_post_office_fixed_trip_post_office_trip FOREIGN KEY (post_office_trip_id) REFERENCES post_office_trip(post_office_trip_id)
);

CREATE TABLE public.post_office_fixed_trip_execute (
	post_office_fixed_trip_execute_id uuid NOT NULL DEFAULT uuid_generate_v1(),
	post_office_fixed_trip_id uuid NOT NULL,
	postman_id uuid NULL,
	departure_date_time timestamp NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT now(),
	status varchar(20) NULL DEFAULT 'WAITING'::character varying,
	arrived_date_time timestamp NULL,
	CONSTRAINT pk_post_office_fixed_trip_execute_id PRIMARY KEY (post_office_fixed_trip_execute_id),
	CONSTRAINT fk_post_office_fixed_trip_execute_post_office_fixed_trip FOREIGN KEY (post_office_fixed_trip_id) REFERENCES post_office_fixed_trip(post_office_fixed_trip_id)
);

CREATE TABLE public.post_driver (
	post_driver_id uuid NOT NULL,
	post_driver_name varchar(200) NULL,
	created_stamp timestamp NULL DEFAULT now(),
	last_updated_stamp timestamp NULL,
	CONSTRAINT post_driver_pk PRIMARY KEY (post_driver_id)
);


CREATE TABLE public.post_driver_post_office_assignment (
	post_driver_post_office_assignment_id uuid NOT NULL,
	post_driver_id uuid NULL,
	post_office_fixed_trip_id uuid NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT now(),
	CONSTRAINT post_driver_post_office_assignment_pk PRIMARY KEY (post_driver_post_office_assignment_id)
);

CREATE TABLE public.post_ship_order (
	post_ship_order_id uuid NOT NULL DEFAULT uuid_generate_v1(),
	from_customer_id uuid NOT NULL,
	to_customer_id uuid NOT NULL,
	package_name varchar(200) NULL,
	package_type_id varchar(60) NULL,
	weight numeric NULL,
	description text NULL,
	pickup_date timestamp NULL,
	expected_delivery_date timestamp NULL,
	status_id varchar(60) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT now(),
	to_post_office_id varchar(50) NULL,
	from_post_office_id varchar(50) NULL,
	current_post_office_id varchar(100) NULL,
	CONSTRAINT pk_ship_order PRIMARY KEY (post_ship_order_id)
);

CREATE TABLE public.post_ship_order_fixed_trip_post_office_assignment (
	post_ship_order_fixed_trip_post_office_assignment_id uuid NOT NULL DEFAULT uuid_generate_v1(),
	post_office_fixed_trip_execute_id uuid NOT NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT now(),
	post_ship_order_trip_post_office_assignment_id uuid NULL,
	CONSTRAINT pk_post_ship_order_fixed_trip_post_office_assignment PRIMARY KEY (post_ship_order_fixed_trip_post_office_assignment_id)
);


CREATE TABLE public.post_ship_order_postman_last_mile_assignment (
	post_ship_order_postman_last_mile_assignment_id uuid NOT NULL DEFAULT uuid_generate_v1(),
	post_ship_order_id uuid NOT NULL,
	postman_id uuid NOT NULL,
	pickup_delivery varchar(1) NULL,
	status_id varchar(60) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT now(),
	CONSTRAINT pk_post_ship_order_postman_last_mile_assignment_id PRIMARY KEY (post_ship_order_postman_last_mile_assignment_id)
);


CREATE TABLE public.post_ship_order_trip_post_office_assignment (
	post_ship_order_trip_post_office_assignment_id uuid NOT NULL DEFAULT uuid_generate_v1(),
	post_ship_order_id uuid NOT NULL,
	post_office_trip_id uuid NOT NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NULL DEFAULT now(),
	status int4 NULL,
	delivery_order int4 NULL,
	CONSTRAINT pk_post_ship_order_trip_post_office_assignment PRIMARY KEY (post_ship_order_trip_post_office_assignment_id)
);


ALTER TABLE public.post_driver ADD CONSTRAINT post_driver_fk FOREIGN KEY (post_driver_id) REFERENCES party(party_id) ON DELETE RESTRICT;


ALTER TABLE public.post_driver_post_office_assignment ADD CONSTRAINT post_driver_post_office_assignment_fk FOREIGN KEY (post_driver_id) REFERENCES post_driver(post_driver_id);
ALTER TABLE public.post_driver_post_office_assignment ADD CONSTRAINT post_driver_post_office_assignment_fk_1 FOREIGN KEY (post_office_fixed_trip_id) REFERENCES post_office_fixed_trip(post_office_fixed_trip_id);



ALTER TABLE public.post_ship_order_fixed_trip_post_office_assignment ADD CONSTRAINT fk_post_ship_order_fixed_trip_post_office_assignment_fixed_trip FOREIGN KEY (post_office_fixed_trip_execute_id) REFERENCES post_office_fixed_trip_execute(post_office_fixed_trip_execute_id);
ALTER TABLE public.post_ship_order_fixed_trip_post_office_assignment ADD CONSTRAINT post_ship_order_fixed_trip_post_office_assignment_fk FOREIGN KEY (post_ship_order_trip_post_office_assignment_id) REFERENCES post_ship_order_trip_post_office_assignment(post_ship_order_trip_post_office_assignment_id);


-- public.post_ship_order_trip_post_office_assignment foreign keys

ALTER TABLE public.post_ship_order_trip_post_office_assignment ADD CONSTRAINT fk_post_ship_order_trip_post_office_assignment_order FOREIGN KEY (post_ship_order_id) REFERENCES post_ship_order(post_ship_order_id);
ALTER TABLE public.post_ship_order_trip_post_office_assignment ADD CONSTRAINT fk_post_ship_order_trip_post_office_assignment_trip FOREIGN KEY (post_office_trip_id) REFERENCES post_office_trip(post_office_trip_id);
