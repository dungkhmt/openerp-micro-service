INSERT INTO public.security_group
    (group_id, description)
VALUES ('ROLE_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT', 'Group education: thesis defense jury management');

INSERT INTO public.security_permission
    (permission_id, description)
VALUES ('EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT', 'Permission thesis defense jury management');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT', 'MENU', NULL, 'EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT',
'Menu thesis defense jury management');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT_LIST', 'MENU', 'MENU_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT', 'EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT',
'Menu thesis defense jury management view list');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT_CREATE', 'MENU', 'MENU_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT', 'EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT',
'Menu thesis defense jury management create');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description)
VALUES ('MENU_EDUCATION_THESIS_DEFENSE_JURY_STUDENT_MANAGEMENT', 'MENU', 'MENU_EDUCATION_LEARNING_MANAGEMENT_STUDENT', 'EDUCATION_LEARNING_MANAGEMENT_STUDENT',
'Menu thesis defense jury student management');

INSERT INTO public.security_group_permission
    (group_id, permission_id)
VALUES ('ROLE_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT', 'EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT');

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_EDUCATION_THESIS_DEFENSE_JURY_MANAGEMENT', NOW(), NOW());



