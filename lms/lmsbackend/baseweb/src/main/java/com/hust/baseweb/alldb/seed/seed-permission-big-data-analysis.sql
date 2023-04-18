INSERT INTO public.security_group
(group_id, description, last_updated_stamp, created_stamp, group_name)
VALUES('ROLE_BIG_DATA_ANAYSIS', 'Big Data Analysis', NULL, '2021-10-22 14:08:52.934', NULL);

INSERT INTO public.security_permission
(permission_id, description, last_updated_stamp, created_stamp)
VALUES('BIG_DATA_ANALYSIS', 'Big Data Analysis', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_BIG_DATA_ANAYSIS', 'BIG_DATA_ANALYSIS', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, created_stamp)
VALUES('ROLE_FULL_ADMIN', 'BIG_DATA_ANALYSIS', NULL, '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_BIGDATA_ANALYSIS', 'MENU', NULL, 'BIG_DATA_ANALYSIS', 'Menu Big Data Analysis', '2021-10-22 15:39:05.031', '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_BIGDATA_ANALYSIS_VIEW_QUALITY_CHECK', 'MENU', 'MENU_BIGDATA_ANALYSIS', 'BIG_DATA_ANALYSIS', 'Menu View Quality Check - Big Data Analysis', '2021-10-22 15:39:05.031', '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_BIGDATA_ANALYSIS_DEFINE_RULE_QUALITY_CHECK', 'MENU', 'MENU_BIGDATA_ANALYSIS', 'BIG_DATA_ANALYSIS', 'Menu View Quality Check - Big Data Analysis', '2021-10-22 15:39:05.031', '2021-10-22 14:08:52.934');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES('MENU_BIGDATA_ANALYSIS_LIST_DATA_QUALITY_CHECK_MASTER', 'MENU', 'MENU_BIGDATA_ANALYSIS', 'BIG_DATA_ANALYSIS', 'Menu View List Data Quality Check Master - Big Data Analysis', '2021-10-22 15:39:05.031', '2021-10-22 14:08:52.934');
