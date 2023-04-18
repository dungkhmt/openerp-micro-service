INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('TMS_ADMIN_VIEW_REPORT', 'Administrator of facility', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'TMS_ADMIN_VIEW_REPORT', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_TMS_REPORT', 'MENU', NULL, 'TMS_ADMIN_VIEW_REPORT', 'Menu sales report', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_TMS_REPORT_DRIVER', 'MENU', 'MENU_TMS_REPORT', 'TMS_ADMIN_VIEW_REPORT', 'Driver based report', NULL,
        '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_TMS_REPORT_CUSTOMER', 'MENU', 'MENU_TMS_REPORT', 'TMS_ADMIN_VIEW_REPORT', 'Customer based report', NULL,
        '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_TMS_REPORT_FACILITY', 'MENU', 'MENU_TMS_REPORT', 'TMS_ADMIN_VIEW_REPORT', 'Facility based', NULL,
        '2020-03-01 18:54:50.488');

