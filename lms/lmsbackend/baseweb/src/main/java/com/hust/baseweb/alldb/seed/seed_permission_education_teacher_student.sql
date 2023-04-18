-- group
insert into public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'Group education: eduTeacher');

insert into public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_LEARNING_MANAGEMENT_STUDENT', 'Group education: student');

--permission
insert into public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'Permission education eduTeacher');

insert into public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_LEARNING_MANAGEMENT_STUDENT', 'Permission education student');

insert into security_permission (permission_id,description)
values('VIEW_QUIZ_TEST_TEACHER','view quiz test of teacher');

insert into security_permission (permission_id,description)
values('VIEW_ALL_QUIZ_TEST_ADMIN','admin view all quiz test');

--application
insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_LEARNING_MANAGEMENT_STUDENT', 'MENU', NULL, 'EDUCATION_LEARNING_MANAGEMENT_STUDENT',
'Menu learning management for students');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_LEARNING_MANAGEMENT_STUDENT_REGISTER_CLASS', 'MENU', 'MENU_EDUCATION_LEARNING_MANAGEMENT_STUDENT', 'EDUCATION_LEARNING_MANAGEMENT_STUDENT',
'Menu learning management for students: register to a class');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_LEARNING_MANAGEMENT_STUDENT_VIEW_LIST_CLASS', 'MENU', 'MENU_EDUCATION_LEARNING_MANAGEMENT_STUDENT', 'EDUCATION_LEARNING_MANAGEMENT_STUDENT',
'Menu learning management for students: view list of class');


insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'MENU', NULL, 'EDUCATION_TEACHING_MANAGEMENT_TEACHER',
'Menu teaching management for eduTeachers');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'MENU', NULL, 'EDUCATION_TEACHING_MANAGEMENT_TEACHER',
'Screen teaching management for eduTeachers');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER_VIEW_LIST_COURSE', 'MENU', 'MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'EDUCATION_TEACHING_MANAGEMENT_TEACHER',
'Menu teaching management for eduTeachers: view list courses');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER_VIEW_LIST_CLASS', 'MENU', 'MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'EDUCATION_TEACHING_MANAGEMENT_TEACHER',
'Menu teaching management for eduTeachers: view list class');



insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER_CREATE_CLASS', 'MENU', 'MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'EDUCATION_TEACHING_MANAGEMENT_TEACHER',
'Menu teaching management for eduTeachers: create new class');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER_QUIZ_TEST_LIST', 'MENU', 'MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'EDUCATION_TEACHING_MANAGEMENT_TEACHER',
'Menu teaching management for eduTeachers: view list quiz test');

insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_TEACHING_MANAGEMENT_ADMIN_ALL_QUIZ_TEST_LIST', 'MENU', 'MENU_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'VIEW_ALL_QUIZ_TEST_ADMIN',
'Menu teaching management for eduTeachers: view list quiz test');



insert into public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_TEACHING_MANAGEMENT_STUDENT_QUIZ_TEST_LIST', 'MENU', 'MENU_EDUCATION_LEARNING_MANAGEMENT_STUDENT', 'EDUCATION_LEARNING_MANAGEMENT_STUDENT',
'Menu teaching management for students: view list quiz test');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('SCREEN_VIEW_QUIZ_TEST_TEACHER', 'SCREEN', NULL, 'VIEW_QUIZ_TEST_TEACHER', 'Screen teacher view quiz test', NULL, '2021-10-22 15:35:30.537');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'SCREEN', NULL, 'EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'Screen teacher view course, class', NULL, '2021-10-22 15:35:30.537');


-- group permission
insert into public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_TEACHING_MANAGEMENT_TEACHER', 'EDUCATION_TEACHING_MANAGEMENT_TEACHER');

insert into public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_LEARNING_MANAGEMENT_STUDENT', 'EDUCATION_LEARNING_MANAGEMENT_STUDENT');

insert into public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_FULL_ADMIN', 'VIEW_ALL_QUIZ_TEST_ADMIN');

--user_login group

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_TEACHING_MANAGEMENT_TEACHER', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_LEARNING_MANAGEMENT_STUDENT', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_TEACHER', NOW(), NOW());


insert into security_group_permission(group_id,permission_id)
values('ROLE_EDUCATION_TEACHING_MANAGEMENT_TEACHER','VIEW_QUIZ_TEST_TEACHER');
insert into security_group_permission(group_id,permission_id) values('ROLE_FULL_ADMIN','VIEW_QUIZ_TEST_TEACHER');


