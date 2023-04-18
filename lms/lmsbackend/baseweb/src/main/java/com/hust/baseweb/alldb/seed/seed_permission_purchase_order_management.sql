-- group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_PURCHASE_ORDER_MANAGEMENT', 'Group management purchase order');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('PURCHASE_ORDER_MANAGEMENT', 'Permission management purchase order');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PURCHASE_ORDER_MANAGEMENT', 'MENU', NULL, 'PURCHASE_ORDER_MANAGEMENT',
'Menu management purchase order');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PURCHASE_ORDER_MANAGEMENT_LIST', 'MENU', 'MENU_PURCHASE_ORDER_MANAGEMENT', 'PURCHASE_ORDER_MANAGEMENT',
'Menu view list purchase order');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PURCHASE_ORDER_MANAGEMENT_SUPPLIER_LIST', 'MENU', 'MENU_PURCHASE_ORDER_MANAGEMENT', 'PURCHASE_ORDER_MANAGEMENT',
'Menu view list supplier');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PURCHASE_ORDER_MANAGEMENT_PRODUCT_PRICE_PURCHASE_LIST', 'MENU', 'MENU_PURCHASE_ORDER_MANAGEMENT', 'PURCHASE_ORDER_MANAGEMENT',
'Menu view list purchase product price');

--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_PURCHASE_ORDER_MANAGEMENT', 'PURCHASE_ORDER_MANAGEMENT');

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_PURCHASE_ORDER_MANAGEMENT', NOW(), NOW());
