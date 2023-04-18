--group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_LAKE_OWNER', 'Owner of lakes');

INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_LAKE_ADMIN', 'Admin of lakes');

--permissionROLE_LAKE_OWNER
insert into public.security_permission
    (permission_id, description)
VALUES ('CREATE_LAKE', 'Permission to create lake');

insert into public.security_permission
    (permission_id, description)
VALUES ('VIEW_ALL_LAKE', 'Permission to administrator all lakes');

insert into public.security_permission
    (permission_id, description)
VALUES ('VIEW_OWN_LAKE', 'Permission to administrator owned lakes');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_LAKE_MANAGEMENT', 'MENU', NULL, NULL, 'Menu management of lakes');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_CREATE_LAKE', 'MENU', 'MENU_LAKE_MANAGEMENT', 'CREATE_LAKE', 'Menu create lake');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_VIEW_OWN_LAKE', 'MENU', 'MENU_LAKE_MANAGEMENT', 'VIEW_OWN_LAKE', 'Menu view own lakes');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_VIEW_ALL_LAKE', 'MENU', 'MENU_LAKE_MANAGEMENT', 'VIEW_ALL_LAKE', 'Menu view all lakes');

--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_LAKE_OWNER', 'CREATE_LAKE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_LAKE_OWNER', 'VIEW_OWN_LAKE');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_LAKE_ADMIN', 'VIEW_ALL_LAKE');

--user_login security group
INSERT INTO public.user_login_security_group
    (user_login_id,group_id)
VALUES ('admin', 'ROLE_LAKE_OWNER');

INSERT INTO public.user_login_security_group
    (user_login_id,group_id)
VALUES ('admin', 'ROLE_LAKE_ADMIN');




