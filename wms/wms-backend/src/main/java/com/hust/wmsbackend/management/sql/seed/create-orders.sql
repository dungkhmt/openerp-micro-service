-- Use this api to get address_name from latitude and longitude
-- https://nominatim.openstreetmap.org/reverse?lat=21.59592834758231&lon=106.51269239562843&fomat=jsonv2
INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('89138448-f7f5-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', 'Phú Phầy, Chợ Đồn, Tỉnh Bắc Kạn, Việt Nam', 105.64512368414523, 22.06252373582915);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('34ddf646-f7f6-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', CURRENT_TIMESTAMP, 0, 2690000, 2690000, '89138448-f7f5-11ed-87f5-02420a00030a', 'Nguyen Van Huy', '0968352942', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CREATED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '34ddf646-f7f6-11ed-87f5-02420a00030a', 'fa15f3af-dcfa-4ffc-9f99-1c7b37b9c3dc', 1, 2690000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('d0d8f988-f7f6-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', 'Phố Tràng Bạch, Đông Triều, Tỉnh Quảng Ninh, Việt Nam', 106.64512368414523, 21.06252373582915);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('fd2a3e34-f7f6-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', CURRENT_TIMESTAMP, 0, 7990000, 7990000, 'd0d8f988-f7f6-11ed-87f5-02420a00030a', 'Nguyễn Văn Thịnh', '0968352942', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'CREATED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'fd2a3e34-f7f6-11ed-87f5-02420a00030a', 'c977d13b-6a58-4d0d-a2d3-be48128b0fe3', 1, 7990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('c5a4cfb8-f7f8-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', 'Văn Quan, Tỉnh Lạng Sơn, Việt Nam', 106.51269239562843, 21.90385934029584);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('fdf77672-f7f8-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', CURRENT_TIMESTAMP, 0, 7499000, 7499000, 'c5a4cfb8-f7f8-11ed-87f5-02420a00030a', 'Le Van Manh', '0968352942', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'fdf77672-f7f8-11ed-87f5-02420a00030a', '24730cbc-71dc-43be-8773-4bdfd8e00436', 1, 7499000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('6cc87564-f7fa-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', 'Đồng Bành, Chi Lăng, Tỉnh Lạng Sơn, Việt Nam', 106.51269239562843, 21.59592834758231);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('726813a8-f7fa-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', CURRENT_TIMESTAMP, 0, 5999000, 5999000, '6cc87564-f7fa-11ed-87f5-02420a00030a', 'Nguyễn Đức Hưng', '0968352942', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '726813a8-f7fa-11ed-87f5-02420a00030a', '16eb3684-4477-4afc-9f7e-e9859705e703', 1, 5999000);

-- 23-05-2023
INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('5777b680-f939-11ed-a401-02420a00030a', 'wms_nguyendinhhung', 'Sơn Dương, Tỉnh Tuyên Quang, Việt Nam', 105.29547823476348, 21.69345523485924);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('5e7fb6ee-f939-11ed-a401-02420a00030a', 'wms_nguyendinhhung', CURRENT_TIMESTAMP, 0, 13900000, 13900000, '5777b680-f939-11ed-a401-02420a00030a', 'Nguyễn Đức Thắng', '0968352942', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '5e7fb6ee-f939-11ed-a401-02420a00030a', 'c32de6d7-36d0-40d9-bca9-85ed866c8ba0', 1, 13900000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('b923a362-f939-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Mù Cang Chải, Tỉnh Yên Bái, Việt Nam', 104.29547823476348, 21.69345523485924);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('c34d2764-f939-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 26990000, 26990000, 'b923a362-f939-11ed-a401-02420a00030a', 'Phạm Duy Anh', '0968352523', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'c34d2764-f939-11ed-a401-02420a00030a', 'd05fb8ea-cbdd-4049-9627-7fb0a7b70ad2', 1, 26990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('38e35534-f93a-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Sín Hồ Sán, Si Ma Cai, Tỉnh Lào Cai, Việt Nam', 104.29547823476348, 22.69345523485924);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('3d0c27f8-f93a-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 10990000, 10990000, '38e35534-f93a-11ed-a401-02420a00030a', 'Phạm Duy Phong', '0968352524', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '3d0c27f8-f93a-11ed-a401-02420a00030a', '9cb679ec-0fd7-4c82-a8e9-f7b08bf6fa49', 1, 10990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('aac1d798-f93a-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Đại Từ, Tỉnh Thái Nguyên, Việt Nam', 105.63452368392832, 21.69345523485924);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('afb29378-f93a-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 19490000, 19490000, 'aac1d798-f93a-11ed-a401-02420a00030a', 'Phạm Duy Hải', '0952352524', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'afb29378-f93a-11ed-a401-02420a00030a', 'a4df0b52-3b8b-4dc5-ac95-8f708f7b8ced', 1, 19490000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('1da40dd0-f93b-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Nam Sách, Tỉnh Hải Dương, Việt Nam', 106.33452368392832, 21.08472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('219bec50-f93b-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 16690000, 16690000, '1da40dd0-f93b-11ed-a401-02420a00030a', 'Phạm Duy Thái', '0952352562', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '219bec50-f93b-11ed-a401-02420a00030a', '2b6c9e6e-1c8a-4313-a561-9e037f0553b6', 1, 16690000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('e2821368-f93b-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Lục Nam, Tỉnh Bắc Giang, Việt Nam', 106.33552368392852, 21.18472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('e83d4962-f93b-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 17490000, 17490000, 'e2821368-f93b-11ed-a401-02420a00030a', 'Phạm Duy Mạnh', '0952352523', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'e83d4962-f93b-11ed-a401-02420a00030a', '2c50edd5-5bde-438f-aca3-dadf7190feaf', 1, 17490000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('4beb3054-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Bình Giang, Chí Linh, Tỉnh Hải Dương, Việt Nam', 106.33552368392852, 21.12472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('521784c8-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 9990000, 9990000, '4beb3054-f93d-11ed-a401-02420a00030a', 'Phạm Duy Hoàng', '09523525672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '521784c8-f93d-11ed-a401-02420a00030a', 'c1740f07-7c40-408c-bee2-5a39073a3ff8', 1, 9990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('7dc1aaf4-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Kinh Môn, Tỉnh Hải Dương, Việt Nam', 106.43552368392852, 21.02472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('82a4095e-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 26990000, 26990000, '7dc1aaf4-f93d-11ed-a401-02420a00030a', 'Phạm Duy Cương', '05235225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '82a4095e-f93d-11ed-a401-02420a00030a', 'd05fb8ea-cbdd-4049-9627-7fb0a7b70ad2', 1, 26990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('9efd5d30-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Kinh Môn, Tỉnh Hải Dương, Việt Nam', 106.43552368392852, 21.02472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('a83730ec-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 2990000, 2990000, '9efd5d30-f93d-11ed-a401-02420a00030a', 'Phạm Duy Cường', '05235225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'a83730ec-f93d-11ed-a401-02420a00030a', '7abcf27c-fa37-45d0-920a-e4582eb71fed', 1, 2990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('d7362f1a-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Lộc Bình, Tỉnh Lạng Sơn, Việt Nam', 106.93552368392852, 21.62472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('dbcbb400-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 6990000, 6990000, 'd7362f1a-f93d-11ed-a401-02420a00030a', 'Phạm Duy Hoàng', '09935225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'dbcbb400-f93d-11ed-a401-02420a00030a', 'bc58e6e6-41ad-4c35-9d26-fd93d87bcfce', 1, 6990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('1952340c-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', 'Yên Phong, Tỉnh Bắc Ninh, Việt Nam', 105.93552368392852, 21.22472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('1d531486-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 12990000, 12990000, '1952340c-f93e-11ed-a401-02420a00030a', 'Vũ Việt Anh', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '1d531486-f93e-11ed-a401-02420a00030a', '7c8ebea3-9826-406d-9a6c-9072aff8791d', 1, 12990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('357b102c-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', 'Thành phố Phổ Yên, Tỉnh Thái Nguyên, Việt Nam', 105.73552368392852, 21.42472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('3953a754-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 3900000, 3900000, '357b102c-f93e-11ed-a401-02420a00030a', 'Vũ Việt Anh', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '3953a754-f93e-11ed-a401-02420a00030a', '03cd5a3a-04d0-4e77-8555-6c57f5cadf26', 1, 3900000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('7e958d8c-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', 'QL.37, Sơn Dương, Tỉnh Tuyên Quang, Việt Nam', 105.33552368392852, 21.72472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('849eec3c-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 15490000, 15490000, '7e958d8c-f93e-11ed-a401-02420a00030a', 'Vũ Việt Anh', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '849eec3c-f93e-11ed-a401-02420a00030a', 'f4803d7f-381b-4cf4-8e03-92ae6c13ff9d', 1, 15490000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('b9a5635c-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', 'Kim Bôi, Tỉnh Hòa Bình, Việt Nam', 105.43552368392852, 20.72472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('bfd263f6-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 13900000, 13900000, 'b9a5635c-f93e-11ed-a401-02420a00030a', 'Vũ Việt Anh', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'bfd263f6-f93e-11ed-a401-02420a00030a', '3a1870cb-ec81-4fba-9552-f5e8ca40c3fc', 1, 13900000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('e365bb56-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', 'Nội Cốc, Kim Bảng, Tỉnh Hà Nam, Việt Nam', 105.83552368392852, 20.52472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('e7218b94-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 7499000, 7499000, 'e365bb56-f93e-11ed-a401-02420a00030a', 'Vũ Việt Toàn', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'e7218b94-f93e-11ed-a401-02420a00030a', '24730cbc-71dc-43be-8773-4bdfd8e00436', 1, 7499000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('1a1a34e2-f93f-11ed-a401-02420a00030a', 'wms_vuvietanh', 'Lạc Sơn, Tỉnh Hòa Bình, Việt Nam', 105.53552368392852, 20.52472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('1f25a476-f93f-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 17490000, 17490000, '1a1a34e2-f93f-11ed-a401-02420a00030a', 'Vũ Việt Toàn', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '1f25a476-f93f-11ed-a401-02420a00030a', '2c50edd5-5bde-438f-aca3-dadf7190feaf', 1, 17490000);