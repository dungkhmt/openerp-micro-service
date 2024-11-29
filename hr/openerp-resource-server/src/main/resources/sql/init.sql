create table if not exists public.hr_job_position
(
    position_code      varchar(100) not null
    constraint pk_hr_job_position
    primary key,
    position_name      varchar(500),
    description        text,
    last_updated_stamp timestamp default CURRENT_DATE,
    created_stamp      timestamp default CURRENT_DATE
    );

alter table public.hr_job_position
    owner to postgres;

create table if not exists public.hr_staff_job_position
(
    user_id            varchar(60)  not null
    constraint fk_hr_staff_job_position_user_id
    references public.user_login,
    position_code      varchar(100) not null
    constraint fk_hr_staff_job_position_position_code
    references public.hr_job_position,
    from_date          timestamp    not null,
    thru_date          timestamp,
    last_updated_stamp timestamp default CURRENT_DATE,
    created_stamp      timestamp default CURRENT_DATE,
    constraint hr_staff_job_position_id
    primary key (user_id, position_code, from_date)
    );

alter table public.hr_staff_job_position
    owner to postgres;

create table if not exists public.hr_department
(
    department_code    varchar(100) not null
    constraint pk_hr_department_id
    primary key,
    department_name    varchar(200),
    last_updated_stamp timestamp default CURRENT_DATE,
    created_stamp      timestamp default CURRENT_DATE,
    status             varchar(100)
    );

alter table public.hr_department
    owner to postgres;

create table if not exists public.hr_staff_department
(
    user_id            varchar(60)  not null
    constraint fk_hr_staff_department_user_id
    references public.user_login,
    department_code    varchar(100) not null
    constraint fk_hr_staff_department_department_code
    references public.hr_department,
    from_date          timestamp    not null,
    thru_date          timestamp,
    last_updated_stamp timestamp default CURRENT_DATE,
    created_stamp      timestamp default CURRENT_DATE,
    constraint pk_hr_staff_department_id
    primary key (user_id, department_code, from_date)
    );

alter table public.hr_staff_department
    owner to postgres;

create table if not exists public.hr_checkinout
(
    id                 uuid      default uuid_generate_v1() not null
    constraint pk_hr_checkinout_id
    primary key,
    user_id            varchar(60),
    time_point         timestamp,
    checkinout         varchar(1),
    last_updated_stamp timestamp default CURRENT_DATE,
    created_stamp      timestamp default CURRENT_DATE
    );

alter table public.hr_checkinout
    owner to postgres;

create table if not exists public.hr_staff_salary
(
    user_id            varchar(60) not null
    constraint fk_hr_staff_salary_user_id
    references public.user_login,
    from_date          timestamp   not null,
    thru_date          timestamp,
    salary             integer,
    last_updated_stamp timestamp default CURRENT_DATE,
    created_stamp      timestamp default CURRENT_DATE,
    constraint pk_hr_staff_salary_id
    primary key (user_id, from_date)
    );

alter table public.hr_staff_salary
    owner to postgres;

create table if not exists public.hr_staff
(
    staff_code         varchar(100) not null
    constraint pk_hr_staff
    primary key,
    user_login_id      varchar(60),
    fullname           varchar(200),
    status             varchar(100),
    last_updated_stamp timestamp default CURRENT_DATE,
    created_stamp      timestamp default CURRENT_DATE
    );

alter table public.hr_staff
    owner to postgres;

create table if not exists public.hr_checkpoint_configure
(
    checkpoint_code    varchar(100) not null
    constraint pk_hr_checkpoint_configure_id
    primary key,
    description        text,
    status             varchar(100),
    last_updated_stamp timestamp default CURRENT_DATE,
    created_stamp      timestamp default CURRENT_DATE
    );

alter table public.hr_checkpoint_configure
    owner to postgres;

create table if not exists public.hr_checkpoint_period
(
    id                 uuid      default uuid_generate_v1() not null
    constraint pk_hr_checkpoint_period_id
    primary key,
    name               varchar(100),
    description        text,
    checkpoint_date    varchar(100),
    created_by_user_id varchar(60),
    status             varchar(100),
    last_updated_stamp timestamp default CURRENT_DATE,
    created_stamp      timestamp default CURRENT_DATE
    );

alter table public.hr_checkpoint_period
    owner to postgres;

create table if not exists public.hr_checkpoint_period_configure
(
    checkpoint_code      varchar(100) not null
    constraint fk_hr_checkpoint_period_configure_checkpoint_code
    references public.hr_checkpoint_configure,
    checkpoint_period_id uuid         not null
    constraint fk_hr_checkpoint_period_configure_checkpoint_period_id
    references public.hr_checkpoint_period,
    coefficient          numeric,
    status               varchar(100),
    last_updated_stamp   timestamp default CURRENT_DATE,
    created_stamp        timestamp default CURRENT_DATE,
    constraint pk_hr_checkpoint_period_configure_id
    primary key (checkpoint_code, checkpoint_period_id)
    );

alter table public.hr_checkpoint_period_configure
    owner to postgres;

create table if not exists public.hr_checkpoint_staff
(
    user_id              varchar(60)  not null
    constraint fk_hr_checkpoint_staff_user_id
    references public.user_login,
    checkpoint_code      varchar(100) not null
    constraint fk_hr_checkpoint_staff_checkpoint_code
    references public.hr_checkpoint_configure,
    checkpoint_period_id uuid         not null
    constraint fk_hr_checkpoint_staff_checkpoint_period
    references public.hr_checkpoint_period,
    point                numeric,
    checked_by_user_id   varchar(60),
    last_updated_stamp   timestamp default CURRENT_DATE,
    created_stamp        timestamp default CURRENT_DATE,
    constraint pk_hr_checkpoint_staff_id
    primary key (user_id, checkpoint_code, checkpoint_period_id)
    );

alter table public.hr_checkpoint_staff
    owner to postgres;

