-- Drop table

-- DROP TABLE public.teacher;

CREATE TABLE public.teacher (
	id varchar(60) NOT NULL,
	teacher_name varchar(200) NULL,
	user_login_id varchar(60) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	max_hour_load numeric NULL,
	CONSTRAINT pk_teacher PRIMARY KEY (id)
);


-- Drop table

-- DROP TABLE public.teacher_course;

CREATE TABLE public.teacher_course (
	teacher_id varchar(60) NOT NULL,
	course_id varchar(60) NOT NULL,
	priority int4 NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	score numeric NULL,
	class_type varchar(20) NOT NULL DEFAULT ''::character varying,
	id uuid NULL DEFAULT uuid_generate_v1(),
	CONSTRAINT pk_teacher_course PRIMARY KEY (teacher_id, course_id, class_type),
	CONSTRAINT teacher_course_id_key UNIQUE (id),
	CONSTRAINT fk_teacher_course__course FOREIGN KEY (course_id) REFERENCES edu_course(id),
	CONSTRAINT fk_teacher_course__teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id)
);


-- Drop table

-- DROP TABLE public.plan;

CREATE TABLE public.plan (
	id uuid NOT NULL DEFAULT uuid_generate_v1(),
	created_by varchar(60) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	plan_name varchar(200) NULL,
	CONSTRAINT pk_plan PRIMARY KEY (id),
	CONSTRAINT fk_plan__user_login FOREIGN KEY (created_by) REFERENCES user_login(user_login_id)
);


-- Drop table

-- DROP TABLE public.class_info;

CREATE TABLE public.class_info (
	id varchar(60) NOT NULL,
	plan_id uuid NOT NULL,
	school_name varchar(200) NULL,
	semester_id varchar(60) NULL,
	course_id varchar(60) NULL,
	class_name varchar(200) NULL,
	credit varchar(30) NULL,
	note varchar(200) NULL,
	class_program varchar(60) NULL,
	semester_type varchar(30) NULL,
	enrollment int4 NULL,
	max_enrollment int4 NULL,
	class_type varchar(30) NOT NULL,
	timetable varchar(600) NULL,
	lesson varchar(200) NULL,
	department_id varchar(60) NULL,
	created_by varchar(60) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	hour_load numeric NULL,
	CONSTRAINT pk_class_info PRIMARY KEY (id, plan_id),
	CONSTRAINT fk_class_info__plan FOREIGN KEY (plan_id) REFERENCES plan(id),
	CONSTRAINT fk_class_info__user_login FOREIGN KEY (created_by) REFERENCES user_login(user_login_id)
);


-- Drop table

-- DROP TABLE public.teacher_in_plan;

CREATE TABLE public.teacher_in_plan (
	teacher_id varchar(60) NOT NULL,
	plan_id uuid NOT NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	max_hour_load numeric NULL,
	minimize_number_working_days varchar(1) NULL,
	CONSTRAINT pk_teacher_in_plan PRIMARY KEY (teacher_id, plan_id),
	CONSTRAINT fk_teacher_in_plan__plan FOREIGN KEY (plan_id) REFERENCES plan(id),
	CONSTRAINT fk_teacher_in_plan__teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id)
);


-- Drop table

-- DROP TABLE public.teacher_course_in_plan;

CREATE TABLE public.teacher_course_in_plan (
	priority int4 NULL,
	plan_id uuid NOT NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	score numeric NULL,
	teacher_course_id uuid NOT NULL,
	teacher_id varchar(60) NOT NULL,
	course_id varchar(60) NOT NULL,
	class_type varchar(20) NOT NULL,
	CONSTRAINT pk_teacher_course_in_plan PRIMARY KEY (plan_id, teacher_course_id),
	CONSTRAINT fk_teacher_course_in_plan__plan FOREIGN KEY (plan_id) REFERENCES plan(id),
	CONSTRAINT fk_teacher_course_in_plan__teacher_course FOREIGN KEY (teacher_course_id) REFERENCES teacher_course(id)
);


-- Drop table

-- DROP TABLE public.assignment_solution;

CREATE TABLE public.assignment_solution (
	id uuid NOT NULL DEFAULT uuid_generate_v1(),
	class_id varchar(60) NOT NULL,
	plan_id uuid NOT NULL,
	teacher_id varchar(60) NOT NULL,
	timetable varchar(200),
	created_by varchar(60) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	pinned bool NOT NULL DEFAULT false,
	CONSTRAINT pk_assignment_solution PRIMARY KEY (id),
	CONSTRAINT fk_assignment_solution__user_login FOREIGN KEY (created_by) REFERENCES user_login(user_login_id)
);

