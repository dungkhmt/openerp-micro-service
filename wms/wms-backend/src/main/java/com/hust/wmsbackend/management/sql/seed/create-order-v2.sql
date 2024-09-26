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
VALUES('e365bb56-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', 'Nội Cốc, Kim Bảng, Tỉnh Hà Nam, Việt Nam', 105.83552368392852, 20.52472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('e7218b94-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 7499000, 7499000, 'e365bb56-f93e-11ed-a401-02420a00030a', 'Vũ Việt Toàn', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'e7218b94-f93e-11ed-a401-02420a00030a', '24730cbc-71dc-43be-8773-4bdfd8e00436', 1, 7499000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('6cc87564-f7fa-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', 'Đồng Bành, Chi Lăng, Tỉnh Lạng Sơn, Việt Nam', 106.51269239562843, 21.59592834758231);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('726813a8-f7fa-11ed-87f5-02420a00030a', 'wms_nguyendinhhung', CURRENT_TIMESTAMP, 0, 5999000, 5999000, '6cc87564-f7fa-11ed-87f5-02420a00030a', 'Nguyễn Đức Hưng', '0968352942', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '726813a8-f7fa-11ed-87f5-02420a00030a', '16eb3684-4477-4afc-9f7e-e9859705e703', 1, 5999000);

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
VALUES('1a1a34e2-f93f-11ed-a401-02420a00030a', 'wms_vuvietanh', 'Lạc Sơn, Tỉnh Hòa Bình, Việt Nam', 105.53552368392852, 20.52472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('1f25a476-f93f-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 9990000, 9990000, '1a1a34e2-f93f-11ed-a401-02420a00030a', 'Vũ Việt Toàn', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '1f25a476-f93f-11ed-a401-02420a00030a', 'c1740f07-7c40-408c-bee2-5a39073a3ff8', 1, 9990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('1952340c-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', 'Yên Phong, Tỉnh Bắc Ninh, Việt Nam', 105.93552368392852, 21.22472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('1d531486-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 13900000, 13900000, '1952340c-f93e-11ed-a401-02420a00030a', 'Vũ Việt Anh', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '1d531486-f93e-11ed-a401-02420a00030a', 'c32de6d7-36d0-40d9-bca9-85ed866c8ba0', 1, 13900000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('357b102c-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', 'Thành phố Phổ Yên, Tỉnh Thái Nguyên, Việt Nam', 105.73552368392852, 21.42472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('3953a754-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 26990000, 26990000, '357b102c-f93e-11ed-a401-02420a00030a', 'Vũ Việt Anh', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '3953a754-f93e-11ed-a401-02420a00030a', 'd05fb8ea-cbdd-4049-9627-7fb0a7b70ad2', 1, 26990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('7e958d8c-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', 'QL.37, Sơn Dương, Tỉnh Tuyên Quang, Việt Nam', 105.33552368392852, 21.72472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('849eec3c-f93e-11ed-a401-02420a00030a', 'wms_vuvietanh', CURRENT_TIMESTAMP, 0, 10990000, 10990000, '7e958d8c-f93e-11ed-a401-02420a00030a', 'Vũ Việt Anh', '07735225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '849eec3c-f93e-11ed-a401-02420a00030a', '9cb679ec-0fd7-4c82-a8e9-f7b08bf6fa49', 1, 10990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('e2821368-f93b-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Lục Nam, Tỉnh Bắc Giang, Việt Nam', 106.33552368392852, 21.18472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('e83d4962-f93b-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 4490000, 4490000, 'e2821368-f93b-11ed-a401-02420a00030a', 'Phạm Duy Mạnh', '0952352523', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'e83d4962-f93b-11ed-a401-02420a00030a', '2da97674-c481-4bd6-9dc4-a073c47bc69a', 1, 4490000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('4beb3054-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Bình Giang, Chí Linh, Tỉnh Hải Dương, Việt Nam', 106.33552368392852, 21.12472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('521784c8-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 5990000, 5990000, '4beb3054-f93d-11ed-a401-02420a00030a', 'Phạm Duy Hoàng', '09523525672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '521784c8-f93d-11ed-a401-02420a00030a', '5953e570-4c6c-499e-bd94-c365a8ec56a8', 1, 5990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('7dc1aaf4-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Kinh Môn, Tỉnh Hải Dương, Việt Nam', 106.43552368392852, 21.02472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('82a4095e-f93d-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 9990000, 9990000, '7dc1aaf4-f93d-11ed-a401-02420a00030a', 'Phạm Duy Cương', '05235225672', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');

INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '82a4095e-f93d-11ed-a401-02420a00030a', '72cc77aa-e683-4b19-ae61-be0ebebfa017', 1, 9990000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('38e35534-f93a-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Sín Hồ Sán, Si Ma Cai, Tỉnh Lào Cai, Việt Nam', 104.29547823476348, 22.69345523485924);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('3d0c27f8-f93a-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 3690000, 3690000, '38e35534-f93a-11ed-a401-02420a00030a', 'Phạm Duy Phong', '0968352524', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '3d0c27f8-f93a-11ed-a401-02420a00030a', '8ccfe2ba-2b6a-4029-a29c-58b363951481', 1, 3690000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('aac1d798-f93a-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Đại Từ, Tỉnh Thái Nguyên, Việt Nam', 105.63452368392832, 21.69345523485924);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('afb29378-f93a-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 3290000, 3290000, 'aac1d798-f93a-11ed-a401-02420a00030a', 'Phạm Duy Hải', '0952352524', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'afb29378-f93a-11ed-a401-02420a00030a', '2df1dd6c-7876-478b-bcf7-914b7c1dff16', 1, 3290000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('1da40dd0-f93b-11ed-a401-02420a00030a', 'wms_phamduyanh', 'Nam Sách, Tỉnh Hải Dương, Việt Nam', 106.33452368392832, 21.08472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('219bec50-f93b-11ed-a401-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 490000, 490000, '1da40dd0-f93b-11ed-a401-02420a00030a', 'Phạm Duy Thái', '0952352562', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), '219bec50-f93b-11ed-a401-02420a00030a', 'e0eab9dc-892d-4ed7-b037-6319c1fed688', 1, 490000);

INSERT INTO public.wms_customer_address
(customer_address_id, user_login_id, address_name, longitude, latitude)
VALUES('c295a3b6-fa59-11ed-bf04-02420a00030a', 'wms_phamduyanh', 'Cổ Mệnh, Chí Linh, Tỉnh Hải Dương, Việt Nam', 106.43552368392852, 21.22472938472384);
INSERT INTO public.wms_sale_order_header
(order_id, user_login_id, order_date, delivery_fee, total_product_cost, total_order_cost, customer_address_id, customer_name, customer_phone_number, description, payment_type, order_type, last_updated_stamp, created_stamp, status, approved_by, cancelled_by)
VALUES('c71a551c-fa59-11ed-bf04-02420a00030a', 'wms_phamduyanh', CURRENT_TIMESTAMP, 0, 4490000, 4490000, 'c295a3b6-fa59-11ed-bf04-02420a00030a', 'Nguyễn Thị Hòa', '0952352523', null, 'COD', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'APPROVED', '', '');
INSERT INTO public.wms_sale_order_item
(sale_order_item_id, order_id, product_id, quantity, price_unit)
VALUES(uuid_generate_v1(), 'c71a551c-fa59-11ed-bf04-02420a00030a', '2da97674-c481-4bd6-9dc4-a073c47bc69a', 1, 4490000) ;