--group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_CUSTOMER_ADMIN', 'customer admin group');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('CUSTOMER_CREATE', 'Create customers');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('CUSTOMER_VIEW', 'View customers');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_CUSTOMER', 'MENU', NULL, NULL, 'Menu customer management');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_CUSTOMER_CREATE', 'MENU', 'MENU_CUSTOMER', 'CUSTOMER_CREATE', 'Menu create customer management');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_CUSTOMER_VIEW', 'MENU', 'MENU_CUSTOMER', 'CUSTOMER_VIEW', 'Menu customer management');

--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_CUSTOMER_ADMIN', 'CUSTOMER_CREATE');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_CUSTOMER_ADMIN', 'CUSTOMER_VIEW');

--userlogin security group
INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_CUSTOMER_ADMIN', NOW(), NOW());
