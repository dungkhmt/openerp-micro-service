--group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_WAREHOUSE_ADMIN', 'warehouse group');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('WAREHOUSE_CREATE', 'Create warehouse');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('WAREHOUSE_VIEW', 'View warehouse');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('WAREHOUSE_IMPORT', 'Import to warehouse management');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('WAREHOUSE_EXPORT', 'Export from warehouse management');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('WAREHOUSE_INVENTORY_ITEM', 'Inventory item');


--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_WAREHOUSE', 'MENU', NULL, NULL, 'Menu warehouse management');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_WAREHOUSE_CREATE', 'MENU', 'MENU_WAREHOUSE', 'WAREHOUSE_CREATE', 'Menu create warehouse');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_WAREHOUSE_VIEW', 'MENU', 'MENU_WAREHOUSE', 'WAREHOUSE_VIEW', 'Menu view warehouse');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_WAREHOUSE_IMPORT', 'MENU', 'MENU_WAREHOUSE', 'WAREHOUSE_IMPORT', 'Menu import warehouse');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_WAREHOUSE_EXPORT', 'MENU', 'MENU_WAREHOUSE', 'WAREHOUSE_EXPORT', 'Menu export warehouse');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_WAREHOUSE_INVENTORY_ITEM', 'MENU', 'MENU_WAREHOUSE', 'WAREHOUSE_INVENTORY_ITEM',
        'Menu inventory item warehouse');


--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_WAREHOUSE_ADMIN', 'WAREHOUSE_CREATE');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_WAREHOUSE_ADMIN', 'WAREHOUSE_VIEW');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_WAREHOUSE_ADMIN', 'WAREHOUSE_IMPORT');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_WAREHOUSE_ADMIN', 'WAREHOUSE_EXPORT');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_WAREHOUSE_ADMIN', 'WAREHOUSE_INVENTORY_ITEM');

--userlogin security group

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_WAREHOUSE_ADMIN', NOW(), NOW());
