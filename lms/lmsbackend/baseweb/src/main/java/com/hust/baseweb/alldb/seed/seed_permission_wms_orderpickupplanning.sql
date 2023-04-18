
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('WMS_ORDER_PICKUP_PLANNING', 'Manage order pickup planning');

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_WAREHOUSE_ADMIN', 'WMS_ORDER_PICKUP_PLANNING', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_WMS', 'MENU', NULL, NULL, 'Menu warehouse management system');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_ORDER_PICKUP_PLANNING', 'MENU', 'MENU_WMS', 'WMS_ORDER_PICKUP_PLANNING', 'Menu order pickup planning');
