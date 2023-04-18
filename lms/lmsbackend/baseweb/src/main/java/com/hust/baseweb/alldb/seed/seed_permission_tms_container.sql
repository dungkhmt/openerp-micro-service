insert into public.security_group(group_id, description)
values ('ROLE_TMS_CONTAINER_ADMIN', 'Quản trị vận chuyển container');

INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('TMS_CONTAINER_ADMIN', 'Administrator of container transportation', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'TMS_CONTAINER_ADMIN', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_TMS_CONTAINER_ADMIN', 'TMS_CONTAINER_ADMIN', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_TMS_CONTAINER', 'MENU', NULL, 'TMS_CONTAINER_ADMIN', 'Menu sales report', NULL,
        '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_TMS_CONTAINER_HOME', 'MENU', 'MENU_TMS_CONTAINER', 'TMS_CONTAINER_ADMIN', 'Menu container transport home', NULL,
        '2020-03-01 18:54:50.488');
