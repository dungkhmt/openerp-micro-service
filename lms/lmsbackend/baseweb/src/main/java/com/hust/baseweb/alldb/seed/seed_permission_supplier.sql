
--group
INSERT INTO public.security_group
(group_id, description, last_updated_stamp, created_stamp)
VALUES('ROLE_SUPPLIER_MANAGEMENT', 'Group management supplier', NULL, '2020-08-13 16:38:26.846');


--permission
INSERT INTO public.security_permission
(permission_id, description, last_updated_stamp, created_stamp)
VALUES('SUPPLIER_MANAGEMENT', 'Permission management supplier', NULL, '2020-08-13 16:38:26.846');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_SUPPLIER_MANAGEMENT', 'MENU', NULL, 'SUPPLIER_MANAGEMENT', 'Menu management supplier', NULL, '2020-08-13 16:38:26.846');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_SUPPLIER_MANAGEMENT_CREATE', 'MENU', 'MENU_SUPPLIER_MANAGEMENT', 'SUPPLIER_MANAGEMENT', 'Menu create supplier', NULL, '2020-08-13 16:38:26.846');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_SUPPLIER_MANAGEMENT_LIST', 'MENU', 'MENU_SUPPLIER_MANAGEMENT', 'SUPPLIER_MANAGEMENT', 'Menu view list supplier', NULL, '2020-08-13 16:38:26.846');

--group permission
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_SUPPLIER_MANAGEMENT', 'SUPPLIER_MANAGEMENT', NULL, '2020-08-13 16:38:26.846');

--user login security group
INSERT INTO public.user_login_security_group
(user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES('admin', 'ROLE_SUPPLIER_MANAGEMENT', '2020-08-13 16:38:26.846', '2020-08-13 16:38:26.846');

