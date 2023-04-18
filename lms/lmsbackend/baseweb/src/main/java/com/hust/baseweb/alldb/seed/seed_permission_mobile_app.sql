-- group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_CREATE_DISTRIBUTOR', 'create distributor group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_CREATE_RETAIL_OUTLET', 'create retail outlet group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_UPDATE_POSITION', 'get current possition and push to server group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_VIEW_USER_POSITION', 'monitor current possition of users-app and push to server group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_SALES_ROUTE', 'sales route group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_DELIVERY_ROUTE', 'delivery route group');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_CREATE_SALES_ORDER_FOR_DISTRIBUTOR',
'salesman creates sales order of retail outlet for distributor');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_DISTRIBUTOR_CREATE', 'Create distributor with mobile App');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_RETAIL_OUTLET_CREATE', 'Create retail outlet with mobile App');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_UPDATE_POSITION', 'get current possition and push to server group');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_VIEW_USER_POSITION', 'monitor current possition of users-app and push to server group');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_SALES_ROUTE', 'sales route group');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_DELIVERY_ROUTE', 'delivery route group');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_CREATE_SALES_ORDER_FOR_DISTRIBUTOR',
'salesman creates sales order of retail outlet for distributor');
--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE', 'MENU', NULL, NULL,
'Menu for mobile app');


INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_CREATE_DISTRIBUTOR', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_DISTRIBUTOR_CREATE',
'Menu for creating distributor with mobile app');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_CREATE_RETAIL_OUTLET', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_RETAIL_OUTLET_CREATE',
'Menu for creating retail outlet with mobile app');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_UPDATE_POSITION', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_UPDATE_POSITION', 'get current possition and push to server group');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_VIEW_USER_POSITION', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_VIEW_USER_POSITION', 'monitor current possition of users-app and push to server group');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_SALES_ROUTE', 'MENU','MENU_MOBILE', 'MOBILE_APP_SALES_ROUTE', 'sales route group');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_DELIVERY_ROUTE', 'MENU','MENU_MOBILE', 'MOBILE_APP_DELIVERY_ROUTE', 'delivery route group');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_CREATE_SALES_ORDER_FOR_DISTRIBUTOR','MENU','MENU_MOBILE', 'MOBILE_APP_CREATE_SALES_ORDER_FOR_DISTRIBUTOR',
'salesman creates sales order of retail outlet for distributor');

--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_CREATE_DISTRIBUTOR', 'MOBILE_APP_DISTRIBUTOR_CREATE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_CREATE_RETAIL_OUTLET', 'MOBILE_APP_RETAIL_OUTLET_CREATE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_UPDATE_POSITION', 'MOBILE_APP_UPDATE_POSITION');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_VIEW_USER_POSITION', 'MOBILE_APP_VIEW_USER_POSITION');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_SALES_ROUTE', 'MOBILE_APP_SALES_ROUTE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_DELIVERY_ROUTE', 'MOBILE_APP_DELIVERY_ROUTE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_CREATE_SALES_ORDER_FOR_DISTRIBUTOR', 'MOBILE_APP_CREATE_SALES_ORDER_FOR_DISTRIBUTOR');
