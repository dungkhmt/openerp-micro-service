-- group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_HR_ADMIN', 'human resources group');

-- permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('USER_CREATE', 'Create Users');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('USER_VIEW', 'View Users');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('USER_APPROVE_REGISTRATION', 'Approve User registration');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('SEND_MAIL_TO_USERS', 'Send mail to users');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('PERM_DEPARTMENT_CREATE', 'Create department');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('PERM_DEPARTMENT_VIEW', 'Create department');


--applications
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_USER', 'MENU', NULL, NULL, 'Menu user management',);
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_USER_CREATE', 'MENU', 'MENU_USER', 'USER_CREATE', 'Menu user create');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_USER_LIST', 'MENU', 'MENU_USER', 'USER_VIEW', 'Menu user list');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_USER_APPROVE_REGISTRATION', 'MENU', 'MENU_USER', 'USER_APPROVE_REGISTRATION', 'Menu approve user registration');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_USER_SEND_MAIL_TO_USERS', 'MENU', 'MENU_USER', 'SEND_MAIL_TO_USERS', 'Menu Send mail to users');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_DEPARTMENT', 'MENU', NULL, NULL, 'Menu user management');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_DEPARTMENT_CREATE', 'MENU', 'MENU_DEPARTMENT', 'PERM_DEPARTMENT_CREATE', 'Menu department create');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_DEPARTMENT_LIST', 'MENU', 'MENU_DEPARTMENT', 'PERM_DEPARTMENT_VIEW', 'Menu department list');

-- group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_HR_ADMIN', 'USER_CREATE');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_HR_ADMIN', 'USER_VIEW');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_HR_ADMIN', 'USER_APPROVE_REGISTRATION');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
 VALUES ('ROLE_HR_ADMIN', 'PERM_DEPARTMENT_CREATE');


INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_HR_ADMIN', 'PERM_DEPARTMENT_VIEW');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_FULL_ADMIN', 'SEND_MAIL_TO_USERS');

--userlogin security group

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_HR_ADMIN', NOW(), NOW());
