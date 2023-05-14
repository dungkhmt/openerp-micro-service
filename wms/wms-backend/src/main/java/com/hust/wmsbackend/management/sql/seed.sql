insert into application_type (application_type_id, description, last_updated_stamp, created_stamp)
values ('MENU', 'Menu application type', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- For WMS v2 create facility
insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('WMSv2_CREATE_FACILITY', 'Create new facility', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp,
                         created_stamp)
values ('MENU_WMSv2', 'MENU', NULL, NULL, 'Menu warehouse management version 2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('MENU_WMSv2_CREATE_FACILITY', 'MENU', 'MENU_WMSv2', 'WMSv2_CREATE_FACILITY', 'Menu create new facility',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
--

insert into security_group (group_id, description, last_updated_stamp, created_stamp, group_name)
values ('ROLE_WMSv2_ADMIN', 'WMS v2', current_timestamp, current_timestamp, 'Quản trị kho phiên bản 2')
ON CONFLICT DO NOTHING;

-- For admin testing account
insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_ADMIN', 'WMSv2_CREATE_FACILITY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

insert into user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
values ('admin', 'ROLE_WMSv2_ADMIN', current_timestamp, current_timestamp)
ON CONFLICT DO NOTHING;

insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('WMSv2_VIEW_FACILITY', 'See all facilities', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp,
                         created_stamp)
values ('MENU_WMSv2_VIEW_FACILITY', 'MENU', 'MENU_WMSv2', 'WMSv2_VIEW_FACILITY', 'Menu warehouse management version 2',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_ADMIN', 'WMSv2_VIEW_FACILITY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

insert into party_type (party_type_id, parent_type_id, has_table, description, last_updated_stamp, created_stamp)
values ('PERSON', null, true, 'Person', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
on conflict do nothing;

insert into party (party_id, party_type_id, external_id, description, status_id, created_date, created_by_user_login,
                   last_modified_date, last_modified_by_user_login, is_unread, last_updated_stamp, created_stamp,
                   party_code, name)
values ('287db6a8-2783-11ea-b1c9-54bf64436441', 'PERSON', null, null, null, null, null, null, null, false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, null, null)
on conflict do nothing;

insert into user_login (user_login_id, current_password, otp_secret, client_token, password_hint, is_system, enabled,
                        has_logged_out, require_password_change, disabled_date_time, successive_failed_logins,
                        last_updated_stamp, created_stamp, otp_resend_number, party_id, email)
values ('admin', '$2a$10$FnkpL3dXh8MXx3nD6yzy7egkmHQ5Evk7BxRPgdrSv1Fh0uPUYmoGu', null, null, null, false, true, false,
        false, null, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, '287db6a8-2783-11ea-b1c9-54bf64436441', null)
on conflict do nothing;

insert into person (party_id, first_name, middle_name, last_name, gender, birth_date, last_updated_stamp)
values ('287db6a8-2783-11ea-b1c9-54bf64436441', 'admin first name', 'admin middle name', 'admin last name', null, null, current_timestamp)
on conflict do nothing;

-- For product management
insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('WMSv2_PRODUCT_DETAIL', 'Managing products', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_ADMIN', 'WMSv2_PRODUCT_DETAIL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp,
                         created_stamp)
values ('MENU_WMSv2_PRODUCT_DETAIL', 'MENU', 'MENU_WMSv2', 'WMSv2_PRODUCT_DETAIL', 'Menu for product management in WMS version 2',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

-- For receipt management
insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('WMSv2_RECEIPT_DETAIL', 'Managing products', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_ADMIN', 'WMSv2_RECEIPT_DETAIL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp,
                         created_stamp)
values ('MENU_WMSv2_RECEIPT_DETAIL', 'MENU', 'MENU_WMSv2', 'MENU_WMSv2_PRODUCT_PRICE_CONFIG', 'Menu for product price management in WMS version 2',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;
-----------------------------------------------------------------

-- For product price config
insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('MENU_WMSv2_PRODUCT_PRICE_CONFIG', 'Managing products', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_ADMIN', 'MENU_WMSv2_PRODUCT_PRICE_CONFIG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp,
                         created_stamp)
values ('MENU_WMSv2_PRODUCT_PRICE_CONFIG', 'MENU', 'MENU_WMSv2', 'MENU_WMSv2_PRODUCT_PRICE_CONFIG', 'Menu for product price management in WMS version 2',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;
-----------------------------------------------------------------

-- For user account management
insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values
    ('USER_CREATE', 'Create user permission', current_timestamp, current_timestamp),
    ('USER_VIEW', 'View user permission', current_timestamp, current_timestamp)
    on conflict do nothing;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
values
    ('MENU_USER', 'MENU', null, null, 'Menu user management', current_timestamp, current_timestamp),
    ('MENU_USER_CREATE', 'MENU', 'MENU_USER', 'USER_CREATE', 'Menu user create', current_timestamp, current_timestamp),
    ('MENU_USER_LIST', 'MENU', 'MENU_USER', 'USER_VIEW', 'Menu user list', current_timestamp, current_timestamp)
    on conflict do nothing;

insert into security_group (group_id, description, last_updated_stamp, created_stamp, group_name)
values ('ROLE_FULL_ADMIN', 'Full admin role', current_timestamp, current_timestamp, 'Full admin')
    on conflict do nothing ;
insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_FULL_ADMIN', 'USER_CREATE', current_timestamp, current_timestamp),
       ('ROLE_FULL_ADMIN', 'USER_VIEW', current_timestamp, current_timestamp)
    on conflict do nothing ;
insert into user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
values ('admin', 'ROLE_FULL_ADMIN', current_timestamp, current_timestamp)
    on conflict do nothing ;

insert into application_type (application_type_id, description, last_updated_stamp)
values ('SCREEN', 'Screen application type', current_timestamp);
insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp)
values ('SCREEN_USER_CREATE', 'SCREEN', null, 'USER_CREATE', 'Screen user create', current_timestamp),
       ('SCREEN_USER_UPDATE', 'SCREEN', null, 'USER_CREATE', 'Screen user update', current_timestamp),
       ('SCREEN_USER_LIST', 'SCREEN', null, 'USER_VIEW', 'Screen user view', current_timestamp),
       ('SCREEN_USER_DETAIL', 'SCREEN', null, 'USER_VIEW', 'Screen user detail view', current_timestamp),
       ('SCREEN_USER_EDIT_BUTTON', 'SCREEN', null, 'USER_CREATE', 'Screen user edit button', current_timestamp),
       ('SCREEN_USER_DELETE_BUTTON', 'SCREEN', null, 'USER_CREATE', 'Screen user delete button', current_timestamp);
-----------------------------------------------------------------
-- For customer role
insert into security_group (group_id, description, last_updated_stamp, created_stamp, group_name)
values ('ROLE_WMSv2_CUSTOMER', 'Role khách hàng ở module warehouse management version 2', current_timestamp, current_timestamp, 'Role khách hàng')
on conflict do nothing ;

insert into user_login_security_group (user_login_id, group_id, last_updated_stamp)
values ('admin', 'ROLE_WMSv2_CUSTOMER', current_timestamp)
on conflict do nothing;

insert into status_type (status_type_id, parent_type_id, description, last_updated_stamp)
values ('USER_STATUS', null, 'Users status', current_timestamp)
on conflict do nothing;

insert into status_item (status_id, status_type_id, status_code, description, last_updated_stamp)
values ('USER_REGISTERED', 'USER_STATUS', 'REGISTERED', 'Đã đăng ký', current_timestamp),
       ('USER_APPROVED', 'USER_STATUS', 'APPROVED', 'Đã phê duyệt', current_timestamp);
on conflict do nothing;

insert into status_type (status_type_id, parent_type_id, description, last_updated_stamp)
values ('PARTY_STATUS', null, 'party status', current_timestamp);
insert into status (status_id, status_type_id, status_code, sequence_id, description, last_updated_stamp)
values ('PARTY_ENABLED', 'PARTY_STATUS', 'ENABLED', 0, 'Đã kích hoạt', current_timestamp),
       ('PARTY_DISABLED', 'PARTY_STATUS', 'DISABLEd', 0, 'Đã bị vô hiệu hóa', current_timestamp)
on conflict do nothing;

insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('USER_APPROVE_REGISTRATION', 'Approve user registration', current_timestamp, current_timestamp)
on conflict do nothing;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp)
values ('MENU_USER_APPROVE_REGISTRATION', 'MENU', 'MENU_USER', 'USER_APPROVE_REGISTRATION', 'Menu approve user registration', current_timestamp)
on conflict do nothing;

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_ADMIN', 'USER_APPROVE_REGISTRATION', current_timestamp, current_timestamp)
on conflict do nothing;

-------------------------------------------------
-- For customer product view
insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('WMSv2_CUSTOMER_PRODUCT', 'Permission customer view product screen', current_timestamp, current_timestamp);

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp)
values ('MENU_WMSv2_CUSTOMER_PRODUCT_VIEW', 'MENU', 'MENU_WMSv2', 'WMSv2_CUSTOMER_PRODUCT', 'Menu for customer product view', current_timestamp);

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_CUSTOMER', 'WMSv2_CUSTOMER_PRODUCT', current_timestamp, current_timestamp);

-- For sale management role
insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('WMSv2_SALE_MANAGEMENT', 'Permission for sale management', current_timestamp, current_timestamp)
    on conflict do nothing ;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp)
values ('MENU_WMSv2_SALE_MANAGEMENT_CREATE_RECEIPT_REQUEST', 'MENU', 'MENU_WMSv2', 'WMSv2_SALE_MANAGEMENT', 'Menu for sale management create receipt request', current_timestamp)
    on conflict do nothing ;

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_SALE_MANAGEMENT', 'WMSv2_SALE_MANAGEMENT', current_timestamp, current_timestamp)
    on conflict do nothing ;

insert into security_group (group_id, description, last_updated_stamp, created_stamp, group_name)
values ('ROLE_WMSv2_SALE_MANAGEMENT', 'Role quản lý bán hàng ở module warehouse management version 2', current_timestamp, current_timestamp, 'Role quản lý bán hàng')
    on conflict do nothing ;

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_SALE_MANAGEMENT', 'WMSv2_SALE_MANAGEMENT', current_timestamp, current_timestamp)
    on conflict do nothing ;

insert into user_login_security_group (user_login_id, group_id, last_updated_stamp)
values ('admin', 'ROLE_WMSv2_SALE_MANAGEMENT', current_timestamp);

-- for approve receipt request screen
insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('WMSv2_APPROVE_RECEIPT_REQUEST', 'Permission for Approve receipt request', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_ADMIN', 'WMSv2_APPROVE_RECEIPT_REQUEST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
values ('MENU_WMSv2_APPROVE_RECEIPT_REQUEST', 'MENU', 'MENU_WMSv2', 'WMSv2_APPROVE_RECEIPT_REQUEST', 'Menu for Approve receipt request', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

-- For process receipt request screen
insert into security_permission (permission_id, description, last_updated_stamp, created_stamp)
values ('WMSv2_PROCESS_RECEIPT_REQUEST', 'Permission for Process receipt request', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into security_group_permission (group_id, permission_id, last_updated_stamp, created_stamp)
values ('ROLE_WMSv2_ADMIN', 'WMSv2_PROCESS_RECEIPT_REQUEST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

insert into application (application_id, application_type_id, module_id, permission_id, description, last_updated_stamp, created_stamp)
values ('MENU_WMSv2_PROCESS_RECEIPT_REQUEST', 'MENU', 'MENU_WMSv2', 'WMSv2_PROCESS_RECEIPT_REQUEST', 'Menu for Process receipt request', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT DO NOTHING;

-- For warehouse management warehouse module data
insert into product_category (name)
values ('Tivi'), ('Tủ lạnh'), ('Máy giặt'), ('Gia dụng'), ('Quạt điều hòa'), ('Máy lạnh');

-- TODO: Handle permission latter
INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_ADMIN.WAREHOUSE', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_ADMIN.PRODUCT', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_ADMIN.ORDER', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_ADMIN.PROCESS_RECEIPT', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_APPROVER.RECEIPTS', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_CUSTOMER.PRODUCTS', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_CUSTOMER.CART', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_PERSON', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.SHIPMENTS', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_TRIPS', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.APPROVAL_ORDERS', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_SALE_MANAGER.PRICE_CONFIG', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_SALE_MANAGER.RECEIPT_REQUEST', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_SALE_MANAGER.ORDERS', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('MENU_WMSv2_DELIVERY_PERSON.DELIVERY_TRIP', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.entity_authorization
(id, role_id, description, last_updated, created)
VALUES('SCR_WMSv2_WAREHOUSE.VIEW', 'ADMIN', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
