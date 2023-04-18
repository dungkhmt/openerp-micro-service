INSERT INTO public.security_group
(group_id, description, last_updated_stamp, created_stamp, group_name)
VALUES('ROLE_DATA_ADMIN', 'data admin group', NULL, '2021-10-22 14:08:52.934', NULL);

INSERT INTO public.security_permission
(permission_id, description, last_updated_stamp, created_stamp)
VALUES('DATA_ADMIN', 'Administrate data', NULL, '2021-10-22 14:08:52.934');



INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_FULL_ADMIN', 'DATA_ADMIN', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_DATA_ADMIN', 'DATA_ADMIN', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_DATA_ADMIN', 'MENU', NULL, 'DATA_ADMIN', 'Menu data admin', '2021-10-22 15:39:05.031', '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_DATA_ADMIN_NOTIFICATIONS', 'MENU', 'MENU_DATA_ADMIN', 'DATA_ADMIN', 'Menu data admin', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_DATA_ADMIN_DASHBOARD', 'MENU', 'MENU_DATA_ADMIN', 'DATA_ADMIN', 'Menu data admin', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_DATA_ADMIN_VIEW_COURSE_VIDEO', 'MENU', 'MENU_DATA_ADMIN', 'DATA_ADMIN', 'Menu admin view course video', NULL, '2021-10-22 15:35:30.537');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_DATA_ADMIN_VIEW_LOG_USER_DO_PRATICE_QUIZ', 'MENU', 'MENU_DATA_ADMIN', 'DATA_ADMIN', 'Menu admin view log users quiz', NULL, '2021-10-22 15:35:30.537');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_DATA_ADMIN_VIEW_LEARNING_PROFILE_USERS', 'MENU', 'MENU_DATA_ADMIN', 'DATA_ADMIN', 'Menu admin view learning profile student', NULL, '2021-10-22 15:35:30.537');

--permission for screens security
insert into security_permission (permission_id,description) values('VIEW_LOG_USER_VIEW_COURSE_VIDEO','view log of users viewing video course');
insert into security_permission (permission_id,description) values('VIEW_LOG_USER_DO_QUIZ','view log of users pratice quizs');
insert into security_permission (permission_id,description) values('VIEW_MAIN_DASHBOARD','view main dashboard');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('SCREEN_DATA_ADMIN_VIEW_COURSE_VIDEO', 'SCREEN', NULL, 'VIEW_LOG_USER_VIEW_COURSE_VIDEO', 'Screen admin view course video', NULL, '2021-10-22 15:35:30.537');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('SCREEN_DATA_ADMIN_VIEW_USER_DO_QUIZS', 'SCREEN', NULL, 'VIEW_LOG_USER_DO_QUIZ', 'Screen admin view course video', NULL, '2021-10-22 15:35:30.537');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('SCREEN_DATA_ADMIN_VIEW_MAIN_DASHBOARD', 'SCREEN', NULL, 'VIEW_MAIN_DASHBOARD', 'Screen admin view course video', NULL, '2021-10-22 15:35:30.537');

insert into security_group_permission(group_id,permission_id) values('ROLE_FULL_ADMIN','VIEW_LOG_USER_VIEW_COURSE_VIDEO');
insert into security_group_permission(group_id,permission_id) values('ROLE_FULL_ADMIN','VIEW_LOG_USER_DO_QUIZ');
insert into security_group_permission(group_id,permission_id) values('ROLE_FULL_ADMIN','VIEW_MAIN_DASHBOARD');
