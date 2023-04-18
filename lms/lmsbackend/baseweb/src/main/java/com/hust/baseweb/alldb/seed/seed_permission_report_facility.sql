INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('FACILITY_ADMIN_VIEW_REPORT', 'Administrator of facility', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'FACILITY_ADMIN_VIEW_REPORT', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_FACILITY_REPORT', 'MENU', NULL, 'FACILITY_ADMIN_VIEW_REPORT', 'Menu sales report', NULL,
        '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_FACILITY_REPORT_INVENTORY_ITEM_ON_HAND', 'MENU', 'MENU_FACILITY_REPORT', 'FACILITY_ADMIN_VIEW_REPORT',
        'Ton kho', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT_IMPORT', 'MENU', 'MENU_FACILITY_REPORT', 'FACILITY_ADMIN_VIEW_REPORT', 'Nhap kho', NULL,
        '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT_EXPORT', 'MENU', 'MENU_FACILITY_REPORT', 'FACILITY_ADMIN_VIEW_REPORT', 'Xuat kho', NULL,
        '2020-03-01 18:54:50.488');

