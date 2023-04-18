INSERT INTO public.security_group
(group_id, description, last_updated_stamp, created_stamp, group_name)
VALUES('ROLE_CHAT', 'chat group', NULL, '2021-10-22 14:08:52.934', NULL);

INSERT INTO public.security_permission
(permission_id, description, last_updated_stamp, created_stamp)
VALUES('PERM_CHAT', 'Chat permission', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_FULL_ADMIN', 'PERM_CHAT', NULL, '2021-10-22 14:08:52.934');


INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_CHAT', 'MENU', NULL, 'PERM_CHAT', 'Menu chat', '2021-10-22 15:39:05.031', '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_CHAT_MESSENGER', 'MENU', 'MENU_CHAT', 'PERM_CHAT', 'Menu data admin', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_CHAT_VOICE', 'MENU', 'MENU_CHAT', 'PERM_CHAT', 'Menu chat voice', NULL, '2021-10-22 14:08:52.934');
