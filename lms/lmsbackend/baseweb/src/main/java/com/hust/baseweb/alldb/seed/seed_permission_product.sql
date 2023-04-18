-- group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_PRODUCT_ADMIN', 'product management group');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('PRODUCT_CREATE', 'Create products');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('PRODUCT_VIEW', 'View products');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('PRODUCT_PRICE_CREATE', 'Create product price');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('PRODUCT_PRICE_VIEW', 'View product price');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PRODUCT', 'MENU', NULL, NULL, 'Menu product management');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PRODUCT_CREATE', 'MENU', 'MENU_PRODUCT', 'PRODUCT_CREATE', 'Menu create product');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PRODUCT_VIEW', 'MENU', 'MENU_PRODUCT', 'PRODUCT_VIEW', 'Menu view product');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PRODUCT_PRICE_CREATE', 'MENU', 'MENU_PRODUCT', 'PRODUCT_PRICE_CREATE', 'Menu create product price');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PRODUCT_PRICE_VIEW', 'MENU', 'MENU_PRODUCT', 'PRODUCT_PRICE_VIEW', 'Menu view product price');

--group permision
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_PRODUCT_ADMIN', 'PRODUCT_CREATE');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_PRODUCT_ADMIN', 'PRODUCT_VIEW');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_PRODUCT_ADMIN', 'PRODUCT_PRICE_CREATE');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_PRODUCT_ADMIN', 'PRODUCT_PRICE_VIEW');

--userlogin security group

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_PRODUCT_ADMIN', NOW(), NOW());

