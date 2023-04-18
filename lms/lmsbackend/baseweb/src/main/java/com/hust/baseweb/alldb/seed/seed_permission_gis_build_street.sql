-- group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_MOBILE_APP_BUILD_STREET', 'Group mobile-app build streets');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('MOBILE_APP_BUILD_STREET', 'Permission mobile-app build street');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_BIULD_STREET', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_BUILD_STREET',
'Menu mobile-app build street');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_MOBILE_APP_VIEW_BIULT_STREET', 'MENU', 'MENU_MOBILE', 'MOBILE_APP_BUILD_STREET',
'Menu mobile-app view built street');

--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_MOBILE_APP_BUILD_STREET', 'MOBILE_APP_BUILD_STREET');

