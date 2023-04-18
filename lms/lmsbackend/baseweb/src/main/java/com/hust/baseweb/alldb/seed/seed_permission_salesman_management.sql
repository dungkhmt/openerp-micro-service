
--group
INSERT INTO public.security_group
(group_id, description, last_updated_stamp, created_stamp)
VALUES('ROLE_SALESMAN_MANAGEMENT', 'Group management salesman', NULL, '2020-08-13 16:38:26.846');


--permission
INSERT INTO public.security_permission
(permission_id, description, last_updated_stamp, created_stamp)
VALUES('SALESMAN_MANAGEMENT', 'Permission management salesman', NULL, '2020-08-13 16:38:26.846');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_MENU_SALESMAN_MANAGEMENT', 'MENU', NULL, 'SALESMAN_MANAGEMENT', 'Menu management salesman', NULL, '2020-08-13 16:38:26.846');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_SALESMAN_MANAGEMENT_CREATE', 'MENU', 'MENU_MENU_SALESMAN_MANAGEMENT', 'SALESMAN_MANAGEMENT', 'Menu create salesman', NULL, '2020-08-13 16:38:26.846');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_SALESMAN_MANAGEMENT_LIST', 'MENU', 'MENU_MENU_SALESMAN_MANAGEMENT', 'SALESMAN_MANAGEMENT', 'Menu view list salesman', NULL, '2020-08-13 16:38:26.846');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_SALESMAN_MANAGEMENT_ASSIGN_SALESMAN_2_DISTRIBUTOR', 'MENU', 'MENU_MENU_SALESMAN_MANAGEMENT', 'SALESMAN_MANAGEMENT', 'Menu assign salesman to distributor', NULL, '2020-08-13 16:38:26.846');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_SALESMAN_MANAGEMENT_ASSIGN_SALESMAN_2_RETAIL_OUTLET', 'MENU', 'MENU_MENU_SALESMAN_MANAGEMENT', 'SALESMAN_MANAGEMENT', 'Menu assign salesman to retail outlet', NULL, '2020-08-13 16:38:26.846');

--group permission
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_SALESMAN_MANAGEMENT', 'SALESMAN_MANAGEMENT', NULL, '2020-08-13 16:38:26.846');

--user login security group
INSERT INTO public.user_login_security_group
(user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES('admin', 'ROLE_SALESMAN_MANAGEMENT', '2020-08-13 16:38:26.846', '2020-08-13 16:38:26.846');
