--group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_CREATE_FACILITY', 'Mobile app create facility group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_CREATE_CUSTOMER', 'Mobile app create customer group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_CREATE_PRODUCT', 'Mobile app create product group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_CREATE_SALES_ORDER', 'Mobile app create sales order group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_CREATE_PURCHASE_ORDER', 'Mobile app create purchase order group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_VIEW_PRODUCT_FACILITY_ON_HAND', 'Mobile app view product facility on hand group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_VIEW_PRODUCT_FACILITY_ON_HAND', 'Mobile app view product facility on hand group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_VIEW_ALL_PRODUCT_FACILITY_ON_HAND', 'Mobile app view ALL product facility on hand group');



--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_FACILITY_CREATE', 'Create facility with mobile App');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_CUSTOMER_CREATE', 'Create customer with mobile App');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_PRODUCT_CREATE', 'Create product with mobile App');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_SALES_ORDER_CREATE', 'Create sales order with mobile App');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_PURCHASE_ORDER_CREATE', 'Create purchase order with mobile App');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_VIEW_PRODUCT_FACILITY_ON_HAND', 'View product on hand facility with mobile App');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_VIEW_ALL_PRODUCT_FACILITY_ON_HAND', 'View product on hand ALL facility with mobile App');

--application

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_CREATE_FACILITY', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_FACILITY_CREATE', 'Menu for creating facility with mobile app');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_CREATE_CUSTOMER', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_CUSTOMER_CREATE', 'Menu for creating customer with mobile app');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_CREATE_PRODUCT', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_PRODUCT_CREATE', 'Menu for creating product with mobile app');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_CREATE_SALES_ORDER', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_SALES_ORDER_CREATE', 'Menu for creating sales order with mobile app');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_CREATE_PURCHASE_ORDER', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_PURCHASE_ORDER_CREATE', 'Menu for creating sales order with mobile app');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_VIEW_PRODUCT_FACILITY_ON_HAND', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_VIEW_PRODUCT_FACILITY_ON_HAND', 'Menu view product facility on hand with mobile app');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_VIEW_ALL_PRODUCT_FACILITY_ON_HAND', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_VIEW_ALL_PRODUCT_FACILITY_ON_HAND', 'Menu view ALL product facility on hand with mobile app');


--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_CREATE_FACILITY', 'MOBILE_APP_FACILITY_CREATE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_CREATE_CUSTOMER', 'MOBILE_APP_CUSTOMER_CREATE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_CREATE_PRODUCT', 'MOBILE_APP_PRODUCT_CREATE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_CREATE_SALES_ORDER', 'MOBILE_APP_SALES_ORDER_CREATE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_CREATE_PURCHASE_ORDER', 'MOBILE_APP_PURCHASE_ORDER_CREATE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_VIEW_PRODUCT_FACILITY_ON_HAND', 'MOBILE_APP_VIEW_PRODUCT_FACILITY_ON_HAND');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_VIEW_ALL_PRODUCT_FACILITY_ON_HAND', 'MOBILE_APP_VIEW_ALL_PRODUCT_FACILITY_ON_HAND');

