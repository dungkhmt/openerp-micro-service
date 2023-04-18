-- permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('PERM_CONFIG_PROMO_TAX', 'Config promotion and tax');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_PROMO_TAX', 'MENU', NULL, 'PERM_CONFIG_PROMO_TAX', 'Menu  for config promo tax');

--- group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_SALES_ADMIN', 'PERM_CONFIG_PROMO_TAX');

--user_login security group
INSERT INTO public.user_login_security_group
    (user_login_id,group_id)
VALUES ('admin', 'ROLE_SALES_ADMIN');




