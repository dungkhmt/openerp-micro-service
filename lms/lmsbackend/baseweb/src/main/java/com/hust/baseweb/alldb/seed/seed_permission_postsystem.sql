-- group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_POST_SYSTEM', 'Group for post system');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_POST_SYSTEM_VIEW_VEHICLE', 'Group for view vehicle of post system');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_POST_SYSTEM_VIEW_CUSTOMER', 'Group for view customer of post system');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_POST_SYSTEM_VIEW_POST_OFFICE', 'Group for view post office of post system');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_POST_SYSTEM_VIEW_POST_ORDER', 'Group for view post order of post system');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_POST_SYSTEM_CREATE_POST_ORDER', 'Group for create post order of post system');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_POST_SYSTEM_VIEW_PACKAGE_PICKUP_PLAN', 'Group for view package pickup plan of post system');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_POST_SYSTEM_VIEW_PACKAGE_DELIVERY_PLAN', 'Group for view package delivery plan of post system');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('POST_SYSTEM', 'Permission post system');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('POST_SYSTEM_VIEW_VEHICLE', 'Permission view vehicle of post system');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('POST_SYSTEM_VIEW_CUSTOMER', 'Permission view customer of post system');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('POST_SYSTEM_VIEW_POST_OFFICE', 'Permission view post office of post system');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('POST_SYSTEM_VIEW_POST_ORDER', 'Permission view post order of post system');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('POST_SYSTEM_CREATE_POST_ORDER', 'Permission create post order of post system');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('POST_SYSTEM_VIEW_PACKAGE_PICKUP_PLAN', 'Permission view package pickup plan of post system');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('POST_SYSTEM_VIEW_PACKAGE_DELIVERY_PLAN', 'Permission view package delivery plan of post system');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_POST', 'MENU', NULL, 'POST_SYSTEM',
'Menu post system');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_POST_VIEW_VEHICLE', 'MENU', 'MENU_POST', 'POST_SYSTEM',
'Menu view vehicle of post system');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_POST_VIEW_CUSTOMER', 'MENU', 'MENU_POST', 'POST_SYSTEM',
'Menu view customer of post system');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_POST_VIEW_POST_OFFICE', 'MENU', 'MENU_POST', 'POST_SYSTEM',
'Menu view post office of post system');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_POST_VIEW_POST_ORDER', 'MENU', 'MENU_POST', 'POST_SYSTEM',
'Menu view post order of post system');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_POST_CREATE_POST_ORDER', 'MENU', 'MENU_POST', 'POST_SYSTEM',
'Menu create post order of post system');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_POST_VIEW_PACKAGE_PICKUP_PLAN', 'MENU', 'MENU_POST', 'POST_SYSTEM',
'Menu view package pickup plan of post system');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_POST_VIEW_PACKAGE_DELIVERY_PLAN', 'MENU', 'MENU_POST', 'POST_SYSTEM',
'Menu view package delivery plan of post system');

--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_POST_SYSTEM', 'POST_SYSTEM');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_POST_SYSTEM_VIEW_VEHICLE', 'POST_SYSTEM_VIEW_VEHICLE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_POST_SYSTEM_VIEW_CUSTOMER', 'POST_SYSTEM_VIEW_CUSTOMER');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_POST_SYSTEM_VIEW_POST_OFFICE', 'POST_SYSTEM_VIEW_POST_OFFICE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_POST_SYSTEM_VIEW_POST_ORDER', 'POST_SYSTEM_VIEW_POST_ORDER');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_POST_SYSTEM_CREATE_POST_ORDER', 'POST_SYSTEM_CREATE_POST_ORDER');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_POST_SYSTEM_VIEW_PACKAGE_PICKUP_PLAN', 'POST_SYSTEM_VIEW_PACKAGE_PICKUP_PLAN');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_POST_SYSTEM_VIEW_PACKAGE_DELIVERY_PLAN', 'POST_SYSTEM_VIEW_PACKAGE_DELIVERY_PLAN');

--user login security group
INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_POST_SYSTEM', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_POST_SYSTEM_VIEW_VEHICLE', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_POST_SYSTEM_VIEW_CUSTOMER', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_POST_SYSTEM_VIEW_POST_OFFICE', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_POST_SYSTEM_VIEW_POST_ORDER', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_POST_SYSTEM_CREATE_POST_ORDER', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_POST_SYSTEM_VIEW_PACKAGE_PICKUP_PLAN', NOW(), NOW());

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_POST_SYSTEM_VIEW_PACKAGE_DELIVERY_PLAN', NOW(), NOW());

