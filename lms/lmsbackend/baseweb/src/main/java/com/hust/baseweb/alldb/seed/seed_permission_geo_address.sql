INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('GEO_ADDRESS_ADMIN', 'Creation of Sales Route', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'GEO_ADDRESS_ADMIN', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_GEO_ADDRESS', 'MENU', NULL, NULL, 'Menu Geo and Address', NULL, '2020-03-01 18:54:50.488');
