INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('SALES_ADMIN_VIEW_REPORT', 'Administrator of sales', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'SALES_ADMIN_VIEW_REPORT', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT', 'MENU', NULL, 'SALES_ADMIN_VIEW_REPORT', 'Menu sales report', NULL,
        '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT_CUSTOMER', 'MENU', 'MENU_SALES_REPORT', 'SALES_ADMIN_VIEW_REPORT', 'Customer based Revenue',
        NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT_SALESMAN', 'MENU', 'MENU_SALES_REPORT', 'SALES_ADMIN_VIEW_REPORT', 'Salesman based Revenue',
        NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT_DISTRIBUTOR', 'MENU', 'MENU_SALES_REPORT', 'SALES_ADMIN_VIEW_REPORT',
        'Distributor based Revenue', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT_PRODUCT', 'MENU', 'MENU_SALES_REPORT', 'SALES_ADMIN_VIEW_REPORT', 'Product based Revenue',
        NULL, '2020-03-01 18:54:50.488');
