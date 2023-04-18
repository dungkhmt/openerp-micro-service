INSERT INTO public.enumeration_type
(enumeration_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('PROD_PROMO_RULE', NULL, NULL, NULL, '2020-02-15 21:27:25.628');
INSERT INTO public.enumeration_type
(enumeration_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('PRODUCT_TRANSPORT_CATEGORY', NULL, NULL, NULL, '2020-03-09 15:59:16.836');
INSERT INTO public.enumeration_type
(enumeration_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('DISTANCE_SOURCE', NULL, NULL, NULL, '2020-03-22 23:53:59.100');


INSERT INTO public.enumeration
(enum_id, enum_type_id, enum_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('PROD_PROMO_DISCOUNT_PERCENTAGE', 'PROD_PROMO_RULE', NULL, NULL, NULL, NULL, '2020-02-15 21:28:45.612');
INSERT INTO public.enumeration
(enum_id, enum_type_id, enum_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('KHO', 'PRODUCT_TRANSPORT_CATEGORY', NULL, NULL, NULL, NULL, '2020-03-09 16:01:14.820');
INSERT INTO public.enumeration
(enum_id, enum_type_id, enum_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('LANH', 'PRODUCT_TRANSPORT_CATEGORY', NULL, NULL, NULL, NULL, '2020-03-09 16:01:14.820');
INSERT INTO public.enumeration
(enum_id, enum_type_id, enum_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('DONG', 'PRODUCT_TRANSPORT_CATEGORY', NULL, NULL, NULL, NULL, '2020-03-09 16:01:14.820');
INSERT INTO public.enumeration
(enum_id, enum_type_id, enum_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('GOOGLE', 'DISTANCE_SOURCE', NULL, NULL, NULL, NULL, '2020-03-22 23:55:50.746');
INSERT INTO public.enumeration
(enum_id, enum_type_id, enum_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('HAVERSINE', 'DISTANCE_SOURCE', NULL, NULL, NULL, NULL, '2020-03-22 23:55:50.746');
INSERT INTO public.enumeration
(enum_id, enum_type_id, enum_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('OPEN_STREET_MAP', 'DISTANCE_SOURCE', NULL, NULL, NULL, NULL, '2020-03-22 23:55:50.746');


INSERT INTO party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
VALUES ('AUTOMATED_AGENT', NULL, FALSE, 'Automated Agent', NOW(), NOW());
INSERT INTO party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
VALUES ('PERSON', NULL, TRUE, 'Person', NOW(), NOW());
INSERT INTO party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
VALUES ('PARTY_GROUP', NULL, TRUE, 'Party Group', NOW(), NOW());
INSERT INTO party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
VALUES ('BANK', 'PARTY_GROUP', TRUE, 'Bank', NOW(), NOW());
INSERT INTO party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
VALUES ('LEGAL_ORGANIZATION', 'PARTY_GROUP', FALSE, 'Legal Organization', '2017-01-03 10:11:27.885',
        '2017-01-03 10:11:27.608');
INSERT INTO party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
VALUES ('CORPORATION', 'LEGAL_ORGANIZATION', FALSE, 'Corporation', NOW(), NOW());
INSERT INTO party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
VALUES ('CUSTOMER_GROUP', 'PARTY_GROUP', FALSE, 'Customer Group', NOW(), NOW());
INSERT INTO party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
VALUES ('PARTY_DISTRIBUTOR', NULL, FALSE, 'Distributor', NOW(), NOW());
INSERT INTO party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
VALUES ('PARTY_RETAIL_OUTLET', NULL, FALSE, 'Distributor', NOW(), NOW());
insert into party_type(party_type_id, description)
values ('COMPANY', 'Company');


INSERT INTO status_type (status_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('PARTY_STATUS', NULL, 'Party status', NOW(), NOW());
INSERT INTO status_type (status_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('MARRY_STATUS', NULL, 'Marry status', NOW(), NOW());
INSERT INTO status_type (status_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('SERVICE_STATUS', NULL, 'Service status', NOW(), NOW());
INSERT INTO status_type (status_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('INVOICE_STATUS', NULL, 'invoice Status', NOW(), NOW());
INSERT INTO status (status_id, status_type_id, status_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('SINGLE', 'MARRY_STATUS', 'SINGLE', 0, 'Độc thân', NOW(), NOW());
INSERT INTO status (status_id, status_type_id, status_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('MARRIED', 'MARRY_STATUS', 'MARRIED', 0, 'Đã kết hôn', NOW(), NOW());
INSERT INTO status (status_id, status_type_id, status_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('DIVORCED', 'MARRY_STATUS', 'DIVORCED', 0, 'Đã ly dị', NOW(), NOW());
INSERT INTO status (status_id, status_type_id, status_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('PARTY_ENABLED', 'PARTY_STATUS', 'ENABLED', 0, 'Đã kích hoạt', NOW(), NOW());
INSERT INTO status (status_id, status_type_id, status_code, sequence_id, description, last_updated_stamp, created_stamp)
VALUES ('PARTY_DISABLED', 'PARTY_STATUS', 'DISABLED', 0, 'Đã bị vô hiệu hóa', NOW(), NOW());

INSERT INTO public.security_group
    (group_id, description, last_updated_stamp, created_stamp)
VALUES ('ROLE_SALE_MANAGER', 'Sale manager account owner access security group', '2017-01-03 10:12:23.879',
        '2017-01-03 10:12:23.878');
INSERT INTO public.security_group
    (group_id, description, last_updated_stamp, created_stamp)
VALUES ('ROLE_ACCOUNTANT', 'Accountant account owner access security group', '2017-01-03 10:12:42.531',
        '2017-01-03 10:12:42.507');
INSERT INTO public.security_group
    (group_id, description, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'Full Admin group, has all general functional permissions.', '2017-01-03 10:12:23.994',
        '2017-01-03 10:12:23.993');
INSERT INTO public.security_group
    (group_id, description, last_updated_stamp, created_stamp)
VALUES ('ROLE_TMS_MANAGER', 'Management of Transportation System', NULL, '2020-03-01 18:39:19.097');
INSERT INTO public.security_group
    (group_id, description, last_updated_stamp, created_stamp)
VALUES ('ROLE_SALES_ROUTE_MANAGER', 'Management of Sales Route', NULL, '2020-03-01 18:54:50.488');

INSERT INTO public.security_group
    (group_id, description, last_updated_stamp, created_stamp)
VALUES ('ROLE_TMS_CONTAINER_ADMIN', 'Quản trị vận chuyển container', NULL, '2020-04-10 23:35:36.604');



INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('USER_CREATE', 'Create user permission', '2019-12-26 08:00:31.803', '2019-12-26 08:00:31.803');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('USER_VIEW', 'View user permission', '2019-12-26 08:00:31.803', '2019-12-26 08:00:31.803');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('ORDER_CREATE', 'Create order permission', '2019-12-26 08:00:31.803', '2019-12-26 08:00:31.803');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('ORDER_VIEW', 'View order permission', '2019-12-26 08:00:31.803', '2019-12-26 08:00:31.803');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('INVOICE_CREATE', 'Create order permission', '2019-12-26 08:00:31.803', '2019-12-26 08:00:31.803');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('INVOICE_VIEW', 'View order permission', '2019-12-26 08:00:31.803', '2019-12-26 08:00:31.803');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('DELIVERY_PLAN_CREATE', 'Creation of delivery plan and trips', NULL, '2020-03-01 18:39:22.008');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('SALES_ROUTE_PLAN_CREATE', 'Creation of Sales Route', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('GEO_ADDRESS_ADMIN', 'Creation of Sales Route', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('SALES_ADMIN_VIEW_REPORT', 'Administrator of sales', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('FACILITY_ADMIN_VIEW_REPORT', 'Administrator of facility', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('TMS_ADMIN_VIEW_REPORT', 'Administrator of facility', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('TMS_CONTAINER_ADMIN', 'Administrator of container transportation', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_permission
    (permission_id, description, last_updated_stamp, created_stamp)
VALUES ('SALES_ADMIN', 'Administrator of sales', NULL, '2020-03-01 18:54:50.488');



INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'ORDER_VIEW', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'ORDER_CREATE', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'USER_CREATE', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'USER_VIEW', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'INVOICE_CREATE', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'INVOICE_VIEW', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_SALE_MANAGER', 'ORDER_CREATE', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_SALE_MANAGER', 'ORDER_VIEW', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_ACCOUNTANT', 'ORDER_VIEW', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_ACCOUNTANT', 'INVOICE_CREATE', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_ACCOUNTANT', 'INVOICE_VIEW', '2019-12-26 08:00:35.749', '2019-12-26 08:00:35.749');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_TMS_MANAGER', 'DELIVERY_PLAN_CREATE', NULL, '2020-03-01 18:39:24.741');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'DELIVERY_PLAN_CREATE', NULL, '2020-03-01 18:49:58.969');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_SALES_ROUTE_MANAGER', 'SALES_ROUTE_PLAN_CREATE', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'SALES_ROUTE_PLAN_CREATE', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'GEO_ADDRESS_ADMIN', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'SALES_ADMIN_VIEW_REPORT', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'FACILITY_ADMIN_VIEW_REPORT', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'TMS_ADMIN_VIEW_REPORT', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_FULL_ADMIN', 'TMS_CONTAINER_ADMIN', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.security_group_permission
    (group_id, permission_id, last_updated_stamp, created_stamp)
VALUES ('ROLE_TMS_CONTAINER_ADMIN', 'TMS_CONTAINER_ADMIN', NULL, '2020-03-01 18:54:50.488');



INSERT INTO application_type(application_type_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU', 'Menu application type', NOW(), NOW());
INSERT INTO application_type(application_type_id, description, last_updated_stamp, created_stamp)
VALUES ('SCREEN', 'Screen application type', NOW(), NOW());
INSERT INTO application_type(application_type_id, description, last_updated_stamp, created_stamp)
VALUES ('MODULE', 'Module application type', NOW(), NOW());
INSERT INTO application_type(application_type_id, description, last_updated_stamp, created_stamp)
VALUES ('SERVICE', 'Service application type', NOW(), NOW());
INSERT INTO application_type(application_type_id, description, last_updated_stamp, created_stamp)
VALUES ('ENTITY', 'Entity application type', NOW(), NOW());

INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_USER', 'MENU', NULL, NULL, 'Menu user management', '2019-12-26 08:00:39.953', '2019-12-26 08:00:39.953');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_USER_CREATE', 'MENU', 'MENU_USER', 'USER_CREATE', 'Menu user create', '2019-12-26 08:00:39.953',
        '2019-12-26 08:00:39.953');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_USER_LIST', 'MENU', 'MENU_USER', 'USER_VIEW', 'Menu user list', '2019-12-26 08:00:39.953',
        '2019-12-26 08:00:39.953');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_ORDER', 'MENU', NULL, NULL, 'Menu order management', '2019-12-26 08:00:39.953',
        '2019-12-26 08:00:39.953');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_ORDER_LIST', 'MENU', 'MENU_ORDER', 'ORDER_VIEW', 'Menu order list', '2019-12-26 08:00:39.953',
        '2019-12-26 08:00:39.953');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_ORDER_CREATE', 'MENU', 'MENU_ORDER', 'ORDER_CREATE', 'Menu order create', '2019-12-26 08:00:39.953',
        '2019-12-26 08:00:39.953');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_INVOICE', 'MENU', NULL, NULL, 'Menu invoice management', '2019-12-26 08:00:39.953',
        '2019-12-26 08:00:39.953');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_INVOICE_CREATE', 'MENU', 'MENU_INVOICE', 'INVOICE_CREATE', 'Menu invoice create',
        '2019-12-26 08:00:39.953', '2019-12-26 08:00:39.953');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_INVOICE_LIST', 'MENU', 'MENU_INVOICE', 'INVOICE_VIEW', 'Menu invoice list', '2019-12-26 08:00:39.953',
        '2019-12-26 08:00:39.953');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_TMS_CREATE_DELIVERY_PLAN', 'MENU', 'MENU_TMS', 'DELIVERY_PLAN_CREATE', 'Menu Create Delivery Plan', NULL,
        '2020-03-01 18:41:10.706');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_TMS', 'MENU', NULL, NULL, 'Menu TMS', '2020-03-01 18:46:31.727', '2020-03-01 18:40:20.478');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_ROUTE', 'MENU', NULL, NULL, 'Menu Sales Route', NULL, '2020-03-01 18:54:50.488');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_ROUTE_PLAN_CREATE', 'MENU', 'MENU_SALES_ROUTE', 'SALES_ROUTE_PLAN_CREATE', 'Menu Sales Route', NULL,
        '2020-03-01 18:54:50.488');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_GEO_ADDRESS', 'MENU', NULL, 'GEO_ADDRESS_ADMIN', 'Menu Geo and Address', '2020-03-23 11:14:44.243',
        '2020-03-01 18:54:50.488');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT', 'MENU', NULL, 'SALES_ADMIN_VIEW_REPORT', 'Menu sales report', NULL,
        '2020-03-01 18:54:50.488');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT_CHART', 'MENU', 'MENU_SALES_REPORT', 'SALES_ADMIN_VIEW_REPORT', 'Chart report', NULL,
        '2020-03-01 18:54:50.488');
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_SALES_REPORT_TABLE', 'MENU', 'MENU_SALES_REPORT', 'SALES_ADMIN_VIEW_REPORT', 'Table report', NULL,
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
INSERT INTO public.application
(application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
VALUES ('MENU_TMS_CONTAINER', 'MENU', NULL, 'TMS_CONTAINER_ADMIN', 'Menu sales report', NULL,
        '2020-03-01 18:54:50.488');



INSERT INTO party (party_id, party_type_id, external_id, description, status_id, created_date, created_by_user_login,
                   last_modified_date, last_modified_by_user_login, is_unread, last_updated_stamp, created_stamp,
                   party_code)
VALUES ('bd6322f2-2121-11ea-81a8-979e2f76b5a4', 'PERSON', NULL, NULL, 'PARTY_ENABLED', NULL, NULL, NULL, NULL, FALSE,
        NOW(), NOW(), 'admin');
INSERT INTO person (party_id, first_name, middle_name, last_name, gender, birth_date, last_updated_stamp, created_stamp)
VALUES ('bd6322f2-2121-11ea-81a8-979e2f76b5a4', 'admin', ',', ',', 'M', NOW(), null, NOW());
INSERT INTO user_login (user_login_id, current_password, password_hint, is_system, enabled, has_logged_out,
                        require_password_change, disabled_date_time, successive_failed_logins, last_updated_stamp,
                        created_stamp, party_id)
VALUES ('admin', '$2a$10$Y4FXX6TalapgQ3rJoe.QHe9.RutM4l81pAm2S1XzDuUR83qLvDxyO', NULL, FALSE, TRUE, FALSE, FALSE, NULL,
        NULL, NOW(), NOW(), 'bd6322f2-2121-11ea-81a8-979e2f76b5a4');

INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('admin', 'ROLE_FULL_ADMIN', NOW(), NOW());

insert into party(party_type_id, status_id)
values ('COMPANY', 'PARTY_ENABLED');



insert into product_type(product_type_id, description)
values ('FINISHED_GOOD', 'finished goods');

insert into uom_type(uom_type_id, description)
values ('WEIGHT_MEASURE', 'Weight'),
       ('LENGTH_MEASURE', 'Length'),
       ('UNIT_MEASURE', 'Unit'),
       ('CURRENCY_MEASURE', 'Currency Measure')
;


insert into uom(uom_id, uom_type_id, description)
values ('WT_kg', 'WEIGHT_MEASURE', 'Kg'),
       ('WT_g', 'WEIGHT_MEASURE', 'g'),
       ('WT_package', 'UNIT_MEASURE', 'gói'),
       ('WT_box', 'UNIT_MEASURE', 'hộp'),
       ('WT_jar', 'UNIT_MEASURE', 'lọ'),
       ('CUR_vnd', 'CURRENCY_MEASURE', 'VND')
;


insert into role_type(role_type_id, description)
values ('BILL_TO_CUSTOMER', 'Hóa đơn đến khách hàng');
insert into role_type(role_type_id, description)
values ('SALES_EXECUTIVE', 'Hóa đơn của nhân viên bán hàng');
insert into role_type(role_type_id, description)
values ('CREATE_DELIVERY_TRIP', 'Tạo chuyến giao hàng');
insert into role_type(role_type_id, description)
values ('SALESMAN_SELL_FROM_DISTRIBUTOR', 'Nhân viên bán hàng bán từ nhà phân phối');
insert into role_type(role_type_id, description)
values ('SALESMAN_SELL_TO_RETAIL_OUTLET', 'Nhân viên bán hàng đến đại lí bán lẻ');
insert into role_type(role_type_id, description)
values ('SUPERVISOR_OF_SALESMAN', 'Giám sát của nhân viên bán hàng');
insert into role_type(role_type_id, description)
values ('HEAD_DEPARTMENT', 'Trưởng phòng');
insert into role_type(role_type_id, description)
values ('EMPLOYEE_DEPARTMENT', 'Nhân viên của phòng');
insert into role_type(role_type_id, description)
values ('SALESMAN_SELL_FROM_FACILITY', 'Nhân viên bán hàng của kho');



INSERT INTO content_type (content_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('DOCUMENT', NULL, 'Document', '2017-01-03 10:11:28.217', '2017-01-03 10:11:28.217');





