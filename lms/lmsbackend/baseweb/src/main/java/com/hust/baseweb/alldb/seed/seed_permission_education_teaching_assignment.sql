-- group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_TEACHING_ASSIGNMENT', 'Group education: teaching assignment');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_VIEW_LIST_SEMESTER', 'Group education: view list semester');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_VIEW_LIST_COURSE', 'Group education: view list course');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_VIEW_LIST_TEACHER', 'Group education: view list eduTeacher');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_VIEW_LIST_CLASS', 'Group education: view list class');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_VIEW_LIST_ASSIGNMENT', 'Group education: view list assignment');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_ASSIGNMENT_EXECUTION', 'Group education: execution assignment');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_TEACHING_ASSIGNMENT', 'Permission education teaching assignment');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_VIEW_LIST_SEMESTER', 'Permission education view list semester');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_VIEW_LIST_COURSE', 'Permission education view list course');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_VIEW_LIST_CLASS', 'Permission education view list class');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_VIEW_LIST_TEACHER', 'Permission education view list eduTeacher');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_VIEW_LIST_ASSIGNMENT', 'Permission education view list assignment');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_ASSIGNMENT_EXECUTION', 'Permission education assignment execution');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_TEACHING_ASSIGNMENT', 'MENU', NULL, 'EDUCATION_TEACHING_ASSIGNMENT',
'Menu teaching assignment');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_VIEW_LIST_SEMESTER', 'MENU', NULL, 'EDUCATION_TEACHING_ASSIGNMENT',
'Menu education: view list semester');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_VIEW_LIST_COURSE', 'MENU', NULL, 'EDUCATION_TEACHING_ASSIGNMENT',
'Menu education: view list course');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_VIEW_LIST_TEACHER', 'MENU', NULL, 'EDUCATION_TEACHING_ASSIGNMENT',
'Menu education: view list eduTeacher');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_VIEW_LIST_CLASS', 'MENU', NULL, 'EDUCATION_TEACHING_ASSIGNMENT',
'Menu education: view list class');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_VIEW_LIST_ASSIGNMENT', 'MENU', NULL, 'EDUCATION_TEACHING_ASSIGNMENT',
'Menu education: view list assignment');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_ASSIGNMENT_EXECUTION', 'MENU', NULL, 'EDUCATION_TEACHING_ASSIGNMENT',
'Menu education: assignment execution');

--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_TEACHING_ASSIGNMENT', 'EDUCATION_TEACHING_ASSIGNMENT');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_VIEW_LIST_SEMESTER', 'EDUCATION_VIEW_LIST_SEMESTER');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_VIEW_LIST_COURSE', 'EDUCATION_VIEW_LIST_COURSE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_VIEW_LIST_CLASS', 'EDUCATION_VIEW_LIST_CLASS');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_VIEW_LIST_TEACHER', 'EDUCATION_VIEW_LIST_TEACHER');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_VIEW_LIST_ASSIGNMENT', 'EDUCATION_VIEW_LIST_ASSIGNMENT');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_ASSIGNMENT_EXECUTION', 'EDUCATION_ASSIGNMENT_EXECUTION');

--user login security group
INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_TEACHING_ASSIGNMENT', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_VIEW_LIST_SEMESTER', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_VIEW_LIST_COURSE', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_VIEW_LIST_TEACHER', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_VIEW_LIST_CLASS', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_VIEW_LIST_ASSIGNMENT', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_ASSIGNMENT_EXECUTION', NOW(), NOW());
