INSERT INTO public.security_group
(group_id, description, last_updated_stamp, created_stamp, group_name)
VALUES('ROLE_WHITE_BOARD', 'white board group', NULL, '2021-10-22 14:08:52.934', NULL);

INSERT INTO public.security_permission
(permission_id, description, last_updated_stamp, created_stamp)
VALUES('WHITE_BOARD', 'White board', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_WHITE_BOARD', 'WHITE_BOARD', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_FULL_ADMIN', 'WHITE_BOARD', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_WHITE_BOARD', 'MENU', NULL, 'WHITE_BOARD', 'Menu white board', '2021-10-22 15:39:05.031', '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_WHITE_BOARD_CREATE', 'MENU', 'MENU_WHITE_BOARD', 'WHITE_BOARD', 'Menu white board create', '2021-10-22 15:39:05.031', '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_WHITE_BOARD_LIST', 'MENU', 'MENU_WHITE_BOARD', 'WHITE_BOARD', 'Menu white board list', '2021-10-22 15:39:05.031', '2021-10-22 14:08:52.934');
