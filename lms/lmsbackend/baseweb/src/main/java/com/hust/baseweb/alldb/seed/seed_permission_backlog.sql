--group
INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_BACKLOG', 'backlog group');

--permission
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('BACKLOG_ASSIGN_SUGGESTION', 'Get suggestion for assigning task');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('BACKLOG_CREATE_PROJECT', 'Create project');
INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('BACKLOG_VIEW_LIST_PROJECT', 'View created projects');

--application
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_BACKLOG', 'MENU', NULL, NULL, 'Menu project backlog and management');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_BACKLOG_ASSIGN_SUGGESTION', 'MENU', 'MENU_BACKLOG', 'BACKLOG_ASSIGN_SUGGESTION', 'Menu suggestion for assigning task');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_BACKLOG_CREATE_PROJECT', 'MENU', 'MENU_BACKLOG', 'BACKLOG_CREATE_PROJECT', 'Menu create project');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_BACKLOG_VIEW_LIST_PROJECT', 'MENU', 'MENU_BACKLOG','BACKLOG_VIEW_LIST_PROJECT', 'Menu view related project');

--group permission
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_BACKLOG', 'BACKLOG_ASSIGN_SUGGESTION');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_BACKLOG', 'BACKLOG_CREATE_PROJECT');
INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_BACKLOG', 'BACKLOG_VIEW_LIST_PROJECT');

--userlogin security group
INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_BACKLOG', NOW(), NOW());
