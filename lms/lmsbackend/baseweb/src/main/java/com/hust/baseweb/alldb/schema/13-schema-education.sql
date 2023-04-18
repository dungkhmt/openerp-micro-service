create table edu_timetable_raw(
    id uuid not null default uuid_generate_v1(),
    semester varchar(20),
    session_id varchar(20),
    class_code varchar(20),
    class_code_follow varchar(20),
    class_type varchar(60),
    course_code varchar(20),
    course_name varchar(256),
    week varchar(40),
    day int,
    time_slot_duration varchar(60),
    start_slot int,
    end_slot int,
    session_day varchar(20),
    room_id varchar(60),
    max_number_students int,
    faculty_id varchar(60),
    constraint pk_edu_timetable_raw primary key(id)
);

create table edu_exam_timetable_raw(
    id uuid not null default uuid_generate_v1(),
    semester varchar(20),
    class_code varchar(20),
    course_code varchar(20),
    course_name varchar(256),
    group varchar(100),
    week varchar(20),
    day varchar(20),
    date varchar(40),
    slot varchar(20),
    number_students int,
    room_id varchar(20),
    constraint pk_edu_timetable_raw primary key(id)
);

create table edu_student(
    student_code varchar(30) not null,
    party_id uuid not null,
    constraint pk_edu_student_code primary key (student_code),
    constraint fk_edu_student_party_id foreign key(party_id) references
);

create table edu_room(
    room_id varchar(60),
    room_name varchar(200),

    constraint pk_edu_room primary key(room_id)
);

-- Drop table

-- DROP TABLE public.edu_department;

create TABLE public.edu_department (
	id varchar(10) NOT NULL,
	department_name varchar(50) NULL,
	CONSTRAINT pk_edu_department PRIMARY KEY (id)
);



-- Drop table

-- DROP TABLE public.edu_course;

create TABLE public.edu_course (
	id varchar(10) NOT NULL,
	course_name varchar(80) NOT NULL,
	credit int2 NOT NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT edu_course_check CHECK ((created_stamp <= last_updated_stamp)),
	CONSTRAINT pk_edu_course PRIMARY KEY (id)
);

create table public.edu_course_chapter(
    chapter_id uuid not null default uuid_generate_v1(),
    course_id varchar(10) not null,
    chapter_name varchar(200) not null,
    status_id varchar(30),
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint edu_course_chapter_pk primary key(chapter_id),
    constraint edu_course_chapter_course_id foreign key(course_id) references edu_course(id)
);

create table public.edu_course_chapter_material(
    edu_course_material_id uuid not null default uuid_generate_v1(),
    chapter_id uuid not null,
    edu_course_material_name varchar(200),
    edu_course_material_type varchar(200),
    source_id uuid ,
    slide_id                 varchar(200),
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    slide_id character varying,
    constraint pk_edu_course_chapter_material primary key(edu_course_material_id),
    constraint fk_edu_course_material_id_chapter_id foreign key(chapter_id) references edu_course_chapter(chapter_id)

);

create table comments_edu_course_material(
    comment_id uuid not null default uuid_generate_v1(),
    comment_message text,
    edu_course_material_id uuid,
    reply_to_comment_id uuid,
    posted_by_user_login_id varchar(60),
    status_id varchar(60),
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint pk_comments_edu_course_material primary key(comment_id),
    constraint fk_comments_edu_course_material_material_id foreign key (edu_course_material_id) references edu_course_chapter_material(edu_course_material_id),
    constraint fk_comments_edu_course_material_material_replay_to_comment_id foreign key (reply_to_comment_id) references comments_edu_course_material(comment_id),
    constraint fk_comments_edu_course_material_user_login_id foreign key(posted_by_user_login_id) references user_login(user_login_id)

);
-- Drop table

-- DROP TABLE public.edu_semester;

create TABLE public.edu_semester (
	id int2 NOT NULL,
	semester_name varchar(60) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	is_active bool NOT NULL DEFAULT false,
	CONSTRAINT edu_semester_check CHECK ((created_stamp <= last_updated_stamp)),
	CONSTRAINT pk_semester PRIMARY KEY (id)
);



-- public.edu_class definition

-- Drop table

-- DROP TABLE edu_class;

create TABLE edu_class (
	id uuid NOT NULL DEFAULT uuid_generate_v1(),
	code int4 NOT NULL,
	class_code varchar(60),
	semester_id int2 NOT NULL,
	course_id varchar(10) NOT NULL,
	class_type varchar(10) NOT NULL,
	status_id varchar(60),
	department_id varchar(10) NOT NULL,
	teacher_id varchar(255) NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT edu_class_check CHECK ((created_stamp <= last_updated_stamp)),
	CONSTRAINT pk_edu_class PRIMARY KEY (id)
);


-- public.edu_class foreign keys

alter table public.edu_class add CONSTRAINT fk_class__user_login FOREIGN KEY (teacher_id) REFERENCES user_login(user_login_id);
alter table public.edu_class add CONSTRAINT fk_class_course FOREIGN KEY (course_id) REFERENCES edu_course(id);
alter table public.edu_class add CONSTRAINT fk_class_department FOREIGN KEY (department_id) REFERENCES edu_department(id);
alter table public.edu_class add CONSTRAINT fk_class_semester FOREIGN KEY (semester_id) REFERENCES edu_semester(id);


create table edu_class_user_login_role(
    class_id uuid not null,
    user_login_id varchar(60) not null,
    role_id varchar(60) not null,
    from_date timestamp not null,
    thru_date timestamp,
    description text,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint pk_edu_class_user_login_id_role primary key(class_id, user_login_id, role_id,from_date),
    constraint fk_edu_class_user_login_user_login_id foreign key (user_login_id) references user_login(user_login_id),
    constraint fk_edu_class_user_login_class_id foreign key (class_id) references edu_class(id)
)

-- public.edu_class_registration definition

-- Drop table

-- DROP TABLE edu_class_registration;

create TABLE edu_class_registration (
	class_id uuid NOT NULL,
	student_id varchar(255) NOT NULL,
	role_id varchar(100) NOT NULL,
	status varchar(20) NOT NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT edu_class_registration_check CHECK ((created_stamp <= last_updated_stamp)),
	CONSTRAINT pk_class_registration PRIMARY KEY (class_id, student_id)
);


-- public.edu_class_registration foreign keys

alter table public.edu_class_registration add CONSTRAINT fk_class_registration__user_login FOREIGN KEY (student_id) REFERENCES userf
_login(user_login_id);
alter table public.edu_class_registration add CONSTRAINT fk_class_registration_class FOREIGN KEY (class_id) REFERENCES edu_class(id);



-- public.edu_assignment definition

-- Drop table

-- DROP TABLE edu_assignment;

create TABLE edu_assignment (
	id uuid NOT NULL DEFAULT uuid_generate_v1(),
	assignment_name varchar(255) NOT NULL,
	subject text NULL,
	class_id uuid NOT NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	dead_line timestamp NOT NULL,
	CONSTRAINT edu_assignment_check CHECK ((created_stamp <= last_updated_stamp)),
	CONSTRAINT edu_assignment_check1 CHECK ((dead_line > created_stamp)),
	CONSTRAINT pk_edu_assignment PRIMARY KEY (id)
);


-- public.edu_assignment foreign keys

alter table public.edu_assignment add CONSTRAINT fk_assignment_class FOREIGN KEY (class_id) REFERENCES edu_class(id);



-- public.edu_assignment_submission definition

-- Drop table

-- DROP TABLE edu_assignment_submission;

create TABLE edu_assignment_submission (
	id uuid NOT NULL DEFAULT uuid_generate_v1(),
	assignment_id uuid NOT NULL,
	student_id varchar(255) NOT NULL,
	original_file_name varchar(255) NOT NULL,
	last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    material_source_mongo_id varchar(255) NULL,
	CONSTRAINT edu_assignment_submission_check CHECK ((created_stamp <= last_updated_stamp)),
	CONSTRAINT pk_assignment_submission PRIMARY KEY (id)
);


-- public.edu_assignment_submission foreign keys

alter table public.edu_assignment_submission add CONSTRAINT fk_assignment_submission__user_login FOREIGN KEY (student_id) REFERENCES user_login(user_login_id);
alter table public.edu_assignment_submission add CONSTRAINT fk_assignment_submission_assignment FOREIGN KEY (assignment_id) REFERENCES edu_assignment(id);



/*create table edu_teacher
(
    teacher_id         varchar(60),
    teacher_name       varchar(200),
    email              varchar(60),
    max_credit         int,
    last_updated_stamp timestamp,
    created_stamp      timestamp,
    constraint pk_edu_teacher primary key (teacher_id)
);

create table edu_course_teacher_preference
(
    course_id          varchar(60),
    teacher_id         varchar(60),
    class_type         varchar(60),
    last_updated_stamp timestamp,
    created_stamp      timestamp,
    constraint pk_edu_course_teacher_preference primary key (course_id, teacher_id, class_type),
    constraint fk_edu_course_teacher_preference_course_id foreign key (course_id) references edu_course (course_id),
    constraint fk_edu_course_teacher_preference_teacher_id foreign key (teacher_id) references edu_teacher (teacher_id)
);
create table edu_class_teacher_asignment
(
    class_id           varchar(60),
    teacher_id         varchar(60),
    last_updated_stamp timestamp,
    created_stamp      timestamp,
    constraint pk_class_teacher_assignment primary key (class_id, teacher_id),
    constraint fk_class_teacher_assignment_teacher_id foreign key (teacher_id) references edu_teacher (teacher_id),
    constraint fk_class_teacher_assignment_class_id foreign key (class_id) references edu_class (class_id)
);*/


alter table public.edu_assignment add open_time timestamp NULL DEFAULT CURRENT_TIMESTAMP;
alter table public.edu_assignment add deleted boolean NOT NULL DEFAULT false;
alter table public.edu_assignment drop CONSTRAINT edu_assignment_check1;
alter table public.edu_assignment rename COLUMN dead_line to close_time;
/*alter table public.edu_assignment add CONSTRAINT edu_assignment_check1 CHECK ((created_stamp <= open_time));
alter table public.edu_assignment add CONSTRAINT edu_assignment_check2 CHECK ((open_time <= close_time));*/


create table comments_edu_assignment(
     comment_id uuid not null default uuid_generate_v1(),
     comment_content text,
     author_by_user_login_id varchar(60),
     replied_comment_id uuid,
     date_post timestamp,
     last_updated_stamp timestamp NULL,
   	 created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

     constraint pk_comment_id primary key(comment_id),
     constraint fk_comment_author_by_user_login_id foreign key(author_by_user_login_id) references user_login(user_login_id)

);

CREATE TABLE public.video (
	id uuid NOT NULL DEFAULT uuid_generate_v1(),
	original_name varchar(255) NULL,
	created_date timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	content_length int8 NOT NULL,
	mime_type varchar(40) NULL,
	last_modified_date timestamp(0) NULL,
	"extension" varchar(20) NULL,
	deleted bool NOT NULL DEFAULT false,
	CONSTRAINT pk_video PRIMARY KEY (id)
);
