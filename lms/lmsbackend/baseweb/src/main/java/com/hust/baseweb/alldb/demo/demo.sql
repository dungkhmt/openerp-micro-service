INSERT INTO party (party_id, party_type_id, external_id, description, status_id, created_date, created_by_user_login,
                   last_modified_date, last_modified_by_user_login, is_unread, last_updated_stamp, created_stamp,
                   party_code)
VALUES ('7e8b6ed2-265c-11ea-aca9-f77013972e0d', 'PERSON', NULL, NULL, 'PARTY_ENABLED', NOW(), 'admin', NULL, NULL,
        FALSE, NULL, NOW(), NULL);
INSERT INTO person (party_id, first_name, middle_name, last_name, gender, birth_date, last_updated_stamp, created_stamp)
VALUES ('7e8b6ed2-265c-11ea-aca9-f77013972e0d', 'Nguyễn', 'Văn', 'Sêu', 'M', NOW(), null, NOW());
INSERT INTO user_login (user_login_id, current_password, password_hint, is_system, enabled, has_logged_out,
                        require_password_change, disabled_date_time, successive_failed_logins, last_updated_stamp,
                        created_stamp, party_id)
VALUES ('nguyenvanseu', '$2a$04$cqFXgdkB.8u2HwT3QUTVZuePtHdzi.rWFCjdgNbVB7l6vn/yAU7F6', NULL, FALSE, TRUE, FALSE, FALSE,
        NULL, NULL, NOW(), NOW(), '7e8b6ed2-265c-11ea-aca9-f77013972e0d');
INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('nguyenvanseu', 'ROLE_SALE_MANAGER', NOW(), NOW());

INSERT INTO party (party_id, party_type_id, external_id, description, status_id, created_date, created_by_user_login,
                   last_modified_date, last_modified_by_user_login, is_unread, last_updated_stamp, created_stamp,
                   party_code)
VALUES ('9a8e40d2-265c-11ea-acaa-eb83ca2329f6', 'PERSON', NULL, NULL, 'PARTY_ENABLED', NOW(), 'admin', NULL, NULL,
        FALSE, NULL, NOW(), NULL);
INSERT INTO person (party_id, first_name, middle_name, last_name, gender, birth_date, last_updated_stamp, created_stamp)
VALUES ('9a8e40d2-265c-11ea-acaa-eb83ca2329f6', 'Trần', 'Thị', 'Toán', 'M', NOW(), null, NOW());
INSERT INTO user_login (user_login_id, current_password, password_hint, is_system, enabled, has_logged_out,
                        require_password_change, disabled_date_time, successive_failed_logins, last_updated_stamp,
                        created_stamp, party_id)
VALUES ('tranthitoan', '$2a$04$cqFXgdkB.8u2HwT3QUTVZuePtHdzi.rWFCjdgNbVB7l6vn/yAU7F6', NULL, FALSE, TRUE, FALSE, FALSE,
        NULL, NULL, NOW(), NOW(), '9a8e40d2-265c-11ea-acaa-eb83ca2329f6');
INSERT INTO user_login_security_group (user_login_id, group_id, last_updated_stamp, created_stamp)
VALUES ('tranthitoan', 'ROLE_ACCOUNTANT', NOW(), NOW());


insert into security_group(group_id, description)
values ('ROLE_TMS_MANAGER', 'Management of Transportation System');
insert into security_permission(permission_id, description)
values ('DELIVERY_PLAN_CREATE', 'Creation of delivery plan and trips');
insert into security_group_permission(group_id, permission_id)
values ('ROLE_TMS_MANAGER', 'DELIVERY_PLAN_CREATE');
insert into security_group_permission(group_id, permission_id)
values ('ROLE_FULL_ADMIN', 'DELIVERY_PLAN_CREATE');
insert into application(application_id, application_type_id, module_id, permission_id, description)
values ('MENU_TMS', 'MENU', null, null, 'Menu TMS');
insert into application(application_id, application_type_id, module_id, permission_id, description)
values ('MENU_TMS_CREATE_DELIVERY_PLAN', 'MENU', 'MENU_TMS', 'DELIVERY_PLAN_CREATE', 'Menu Create Delivery Plan');


insert into security_group(group_id, description)
values ('ROLE_SALES_ROUTE_MANAGER', 'Management of Sales Route');
insert into security_permission(permission_id, description)
values ('SALES_ROUTE_PLAN_CREATE', 'Creation of Sales Route');
insert into security_group_permission(group_id, permission_id)
values ('ROLE_SALES_ROUTE_MANAGER', 'SALES_ROUTE_PLAN_CREATE');
insert into security_group_permission(group_id, permission_id)
values ('ROLE_FULL_ADMIN', 'SALES_ROUTE_PLAN_CREATE');
insert into application(application_id, application_type_id, module_id, permission_id, description)
values ('MENU_SALES_ROUTE', 'MENU', null, null, 'Menu Sales Route');
insert into application(application_id, application_type_id, module_id, permission_id, description)
values ('MENU_SALES_ROUTE_PLAN_CREATE', 'MENU', 'MENU_SALES_ROUTE', 'SALES_ROUTE_PLAN_CREATE', 'Menu Sales Route');


INSERT INTO public.party
(party_id, party_type_id, external_id, description, status_id, created_date, created_by_user_login, last_modified_date,
 last_modified_by_user_login, is_unread, last_updated_stamp, created_stamp, party_code)
VALUES ('8161d37e-4026-11ea-9be3-54bf64436441', 'PARTY_DISTRIBUTOR', NULL, NULL, NULL, NULL, NULL, NULL, NULL, false,
        '2020-01-26 17:27:51.182', '2020-01-26 17:27:51.182', NULL);
INSERT INTO public.party
(party_id, party_type_id, external_id, description, status_id, created_date, created_by_user_login, last_modified_date,
 last_modified_by_user_login, is_unread, last_updated_stamp, created_stamp, party_code)
VALUES ('875704ac-4026-11ea-9be4-54bf64436441', 'PARTY_DISTRIBUTOR', NULL, NULL, NULL, NULL, NULL, NULL, NULL, false,
        '2020-01-26 17:28:01.179', '2020-01-26 17:28:01.179', NULL);
INSERT INTO public.party
(party_id, party_type_id, external_id, description, status_id, created_date, created_by_user_login, last_modified_date,
 last_modified_by_user_login, is_unread, last_updated_stamp, created_stamp, party_code)
VALUES ('5ae20c48-4d6c-11ea-967e-54bf64436441', 'PARTY_RETAIL_OUTLET', NULL, NULL, 'PARTY_ENABLED', NULL, 'admin', NULL,
        NULL, NULL, NULL, '2020-02-12 14:50:36.479', NULL);
INSERT INTO public.party
(party_id, party_type_id, external_id, description, status_id, created_date, created_by_user_login, last_modified_date,
 last_modified_by_user_login, is_unread, last_updated_stamp, created_stamp, party_code)
VALUES ('d8d8fb18-4d6f-11ea-9681-54bf64436441', 'PARTY_RETAIL_OUTLET', NULL, NULL, 'PARTY_ENABLED', NULL, 'admin', NULL,
        NULL, NULL, NULL, '2020-02-12 15:15:36.302', NULL);
INSERT INTO public.party
(party_id, party_type_id, external_id, description, status_id, created_date, created_by_user_login, last_modified_date,
 last_modified_by_user_login, is_unread, last_updated_stamp, created_stamp, party_code)
VALUES ('5b63a1ea-4d71-11ea-9684-54bf64436441', 'PARTY_RETAIL_OUTLET', NULL, NULL, 'PARTY_ENABLED', NULL, 'admin', NULL,
        NULL, NULL, NULL, '2020-02-12 15:26:24.812', NULL);

INSERT INTO public.party_customer
(party_id, customer_name, status_id, description, last_updated_stamp, created_stamp, party_type_id)
VALUES ('8161d37e-4026-11ea-9be3-54bf64436441', 'Nhà phân phối Sóc Sơn', NULL, NULL, NULL, '2020-01-26 17:30:54.982',
        NULL);
INSERT INTO public.party_customer
(party_id, customer_name, status_id, description, last_updated_stamp, created_stamp, party_type_id)
VALUES ('875704ac-4026-11ea-9be4-54bf64436441', 'Nhà phân phối Đông Anh', NULL, NULL, NULL, '2020-01-26 17:30:54.982',
        NULL);
INSERT INTO public.party_customer
(party_id, customer_name, status_id, description, last_updated_stamp, created_stamp, party_type_id)
VALUES ('5ae20c48-4d6c-11ea-967e-54bf64436441', 'VinMart Thăng Long A', NULL, NULL, NULL, '2020-02-12 15:13:10.113',
        NULL);
INSERT INTO public.party_customer
(party_id, customer_name, status_id, description, last_updated_stamp, created_stamp, party_type_id)
VALUES ('d8d8fb18-4d6f-11ea-9681-54bf64436441', 'VinMart Bắc Từ Liêm', NULL, NULL, NULL, '2020-02-12 15:18:13.122',
        NULL);
INSERT INTO public.party_customer
(party_id, customer_name, status_id, description, last_updated_stamp, created_stamp, party_type_id)
VALUES ('5b63a1ea-4d71-11ea-9684-54bf64436441', 'VinMart Trung Hòa', NULL, NULL, NULL, '2020-02-12 15:27:28.832', NULL);

INSERT INTO public.party_contact_mech_purpose
(party_id, contact_mech_id, contact_mech_purpose_type_id, from_date, thru_date, last_updated_stamp, created_stamp)
VALUES ('5ae20c48-4d6c-11ea-967e-54bf64436441', 'f581f14a-4d6d-11ea-9680-54bf64436441', 'PRIMARY_LOCATION',
        '2020-02-12 15:04:40.675', NULL, NULL, '2020-02-12 15:04:40.675');
INSERT INTO public.party_contact_mech_purpose
(party_id, contact_mech_id, contact_mech_purpose_type_id, from_date, thru_date, last_updated_stamp, created_stamp)
VALUES ('d8d8fb18-4d6f-11ea-9681-54bf64436441', '9bd89006-4d70-11ea-9683-54bf64436441', 'PRIMARY_LOCATION',
        '2020-02-12 15:21:35.321', NULL, NULL, '2020-02-12 15:21:35.321');
INSERT INTO public.party_contact_mech_purpose
(party_id, contact_mech_id, contact_mech_purpose_type_id, from_date, thru_date, last_updated_stamp, created_stamp)
VALUES ('5b63a1ea-4d71-11ea-9684-54bf64436441', 'b6a4f66c-4d71-11ea-9686-54bf64436441', 'PRIMARY_LOCATION',
        '2020-02-12 15:30:03.371', NULL, NULL, '2020-02-12 15:30:03.371');


INSERT INTO public.geo_point
    (geo_point_id, longitude, latitude, last_updated_stamp, created_stamp)
VALUES ('c08818e8-4d6d-11ea-967f-54bf64436441', '105.723729', '21.000712', NULL, '2020-02-12 15:00:36.514');
INSERT INTO public.geo_point
    (geo_point_id, longitude, latitude, last_updated_stamp, created_stamp)
VALUES ('4b6745f4-4d70-11ea-9682-54bf64436441', '105.780846', '21.052961', NULL, '2020-02-12 15:18:48.495');
INSERT INTO public.geo_point
    (geo_point_id, longitude, latitude, last_updated_stamp, created_stamp)
VALUES ('9d0e8cd6-4d71-11ea-9685-54bf64436441', '105.801801', '21.007741', NULL, '2020-02-12 15:28:14.984');

INSERT INTO public.postal_address
(contact_mech_id, address, postal_code, geo_point_id, country_geo_id, state_province_geo_id, city, last_updated_stamp,
 created_stamp)
VALUES ('f581f14a-4d6d-11ea-9680-54bf64436441',
        'Khu đô thị VinHomes Thăng Long, đại lộ Thăng Long, phố Lê Trọng Tấn, thành phố Hà Nội', NULL,
        'c08818e8-4d6d-11ea-967f-54bf64436441', NULL, NULL, NULL, NULL, '2020-02-12 15:02:05.392');
INSERT INTO public.postal_address
(contact_mech_id, address, postal_code, geo_point_id, country_geo_id, state_province_geo_id, city, last_updated_stamp,
 created_stamp)
VALUES ('9bd89006-4d70-11ea-9683-54bf64436441',
        'Khu B1 Trung tâm thương mại Vincom Plaza Bắc Từ Liêm, CC Green Stars, số 234 Phạm Văn Đồng, phường Cổ nhuế 1, quận Bắc Từ Liêm, thành phố Hà Nội',
        NULL, '4b6745f4-4d70-11ea-9682-54bf64436441', NULL, NULL, NULL, NULL, '2020-02-12 15:21:03.456');
INSERT INTO public.postal_address
(contact_mech_id, address, postal_code, geo_point_id, country_geo_id, state_province_geo_id, city, last_updated_stamp,
 created_stamp)
VALUES ('b6a4f66c-4d71-11ea-9686-54bf64436441',
        'Tầng hầm B1, N05, khu đô thị Trung Hòa Nhân Chính, phường Hoàng Đạo Thúy, quận Cầu Giấy, thành phố Hà Nội',
        NULL, '9d0e8cd6-4d71-11ea-9685-54bf64436441', NULL, NULL, NULL, NULL, '2020-02-12 15:28:57.913');


insert into facility_type(facility_type_id, description)
values ('WAREHOUSE', 'Warehouse'),
       ('RETAIL_STORE', 'Retail Store');

insert into facility(facility_id, facility_type_id, facility_name)
values ('FAHN00001', 'WAREHOUSE', 'Kho Hà Nội'),
       ('FAHCM00002', 'WAREHOUSE', 'Kho HCM')
;

insert into product(product_id, product_type_id, product_name, quantity_uom_id)
values ('20201260001', 'FINISHED_GOOD', 'Nước mắm chinsu', 'WT_jar'),
       ('20201260002', 'FINISHED_GOOD', 'Tương ớt chinsu', 'WT_jar'),
       ('20201260003', 'FINISHED_GOOD', 'Sữa tươi', 'WT_box'),
       ('20201260004', 'FINISHED_GOOD', 'Mỳ koreno', 'WT_package'),
       ('20201260005', 'FINISHED_GOOD', 'Mỳ hảo hảo', 'WT_package'),
       ('20201260006', 'FINISHED_GOOD', 'Mỳ udon', 'WT_package'),
       ('20201260007', 'FINISHED_GOOD', 'Dầu ăn tường an', 'WT_jar'),
       ('20201260008', 'FINISHED_GOOD', 'Dầu ăn hướng dương', 'WT_jar')
;


insert into party(party_id, party_type_id)
values ('8161d37e-4026-11ea-9be3-54bf64436441', 'PARTY_DISTRIBUTOR'),
       ('875704ac-4026-11ea-9be4-54bf64436441', 'PARTY_DISTRIBUTOR')
;

insert into party_customer(party_id, customer_name)
values ('8161d37e-4026-11ea-9be3-54bf64436441', 'Nhà phân phối Sóc Sơn'),
       ('875704ac-4026-11ea-9be4-54bf64436441', 'Nhà phân phối Đông Anh')
;


insert into product_promo_type(product_promo_type_id)
values ('SALES_PROMO');

insert into order_adjustment_type(order_adjustment_type_id)
values ('DISCOUNT_ADJUSTMENT');

insert into enumeration_type(enumeration_type_id)
values ('PROD_PROMO_RULE');
insert into enumeration(enum_id, enum_type_id)
values ('PROD_PROMO_DISCOUNT_PERCENTAGE', 'PROD_PROMO_RULE');


insert into product_promo(product_promo_id, promo_name, product_promo_type_id, from_date)
values ('4022313c-4ffe-11ea-82dd-54bf64436441', 'CT khuyến mại tết', 'SALES_PROMO', '2020-02-15 10:00:00');

insert into product_promo_rule(product_promo_rule_id, product_promo_id, product_promo_rule_enum_id, json_params)
values ('5c21abae-5000-11ea-82de-54bf64436441', '4022313c-4ffe-11ea-82dd-54bf64436441',
        'PROD_PROMO_DISCOUNT_PERCENTAGE', '{"discountpercentage":0.05}');

insert into product_promo_product(product_promo_rule_id, product_id)
values ('5c21abae-5000-11ea-82de-54bf64436441', '20201260002'),
       ('5c21abae-5000-11ea-82de-54bf64436441', '20201260003'),
       ('5c21abae-5000-11ea-82de-54bf64436441', '20201260004'),
       ('5c21abae-5000-11ea-82de-54bf64436441', '20201260005'),
       ('5c21abae-5000-11ea-82de-54bf64436441', '20201260006'),
       ('5c21abae-5000-11ea-82de-54bf64436441', '20201260007'),
       ('5c21abae-5000-11ea-82de-54bf64436441', '20201260008');



insert into tax_authority_rate_product(tax_auth_rate_type_id, product_id, from_date, tax_percentage)
values ('VAT_TAX_10', '20201260001', '2020-02-15 10:00:00', 10),
       ('VAT_TAX_10', '20201260002', '2020-02-15 10:00:00', 10),
       ('VAT_TAX_10', '20201260003', '2020-02-15 10:00:00', 10),
       ('VAT_TAX_10', '20201260004', '2020-02-15 10:00:00', 10),
       ('VAT_TAX_10', '20201260005', '2020-02-15 10:00:00', 10),
       ('VAT_TAX_10', '20201260006', '2020-02-15 10:00:00', 10),
       ('VAT_TAX_10', '20201260007', '2020-02-15 10:00:00', 10),
       ('VAT_TAX_10', '20201260008', '2020-02-15 10:00:00', 10);
