INSERT INTO public.status_type
(status_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('ORDER_STATUS', NULL, 'Order Status', NULL, '2020-02-01 21:35:10.048');
INSERT INTO public.status_type
(status_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('DELIVERY_STATUS', NULL, 'Delivery status', NULL, '2020-03-08 08:43:46.697');
INSERT INTO public.status_type
(status_type_id, parent_type_id, description, last_updated_stamp, created_stamp)
VALUES ('USER_STATUS', NULL, 'users status', NULL, '2020-03-08 08:43:46.697');


insert into status_item(status_id, status_type_id, status_code, description)
values ('ORDER_CREATED', 'ORDER_STATUS', 'CREATED', 'tạo mới'),
       ('ORDER_CANCELLED', 'ORDER_STATUS', 'CANCELLED', 'đã hủy');

insert into status_item(status_id, status_type_id, description)
values ('SHIPMENT_ITEM_CREATED', 'DELIVERY_STATUS', 'Tạo mới'),
       ('DELIVERY_TRIP_CREATED', 'DELIVERY_STATUS', 'Tạo mới'),
       ('SHIPMENT_ITEM_SCHEDULED_TRIP', 'DELIVERY_STATUS', 'Đang xếp chuyến'),
       ('DELIVERY_TRIP_DETAIL_SCHEDULED_TRIP', 'DELIVERY_STATUS', 'Đang xếp chuyến'),
       ('DELIVERY_TRIP_APPROVED_TRIP', 'DELIVERY_STATUS', 'Đã phê duyệt chuyến'),
       ('DELIVERY_TRIP_DETAIL_APPROVED_TRIP', 'DELIVERY_STATUS', 'Đã phê duyệt chi tiết chuyến'),
       ('DELIVERY_TRIP_DETAIL_ON_TRIP', 'DELIVERY_STATUS', 'Đang thực hiện chuyến'),
       ('DELIVERY_TRIP_EXECUTED', 'DELIVERY_STATUS', 'Đang thực hiện chuyến'),
       ('DELIVERY_TRIP_DETAIL_COMPLETED', 'DELIVERY_STATUS', 'Hoàn thành giao chi tiết chuyến'),
       ('SHIPMENT_ITEM_COMPLETED', 'DELIVERY_STATUS', 'Hoàn thành giao đơn vận chuyển'),
       ('DELIVERY_TRIP_COMPLETED', 'DELIVERY_STATUS', 'Hoàn thành giao chuyến');

insert into status_item(status_id, status_type_id, description)
values ('INVOICE_CREATED', 'INVOICE_STATUS', 'Tạo mới hóa đơn'),
       ('INVOICE_APPROVED', 'INVOICE_STATUS', 'Đã phê duyệt hóa đơn'),
       ('INVOICE_CANCELED', 'INVOICE_STATUS', 'Hóa đơn đã bị hủy'),
       ('INVOICE_COMPLETED', 'INVOICE_STATUS', 'Hóa đơn hoàn thành');

insert into status_item(status_id, status_type_id, status_code, description)
values ('USER_REGISTERED', 'USER_STATUS', 'REGISTERED', 'Đã đăng ký'),
       ('USER_APPROVED', 'USER_STATUS', 'APPROVED', 'Đã phê duyệt');


insert into sales_route_visit_frequency(visit_frequency_id, description, repeat_week)
values ('FW1', '1 tuần thăm 1 lần', 1),
       ('FW2', '1 tuần thăm 2 lần', 1),
       ('FW3', '1 tuần thăm 3 lần', 1),
       ('FW4', '1 tuần thăm 4 lần', 1),
       ('FW5', '1 tuần thăm 5 lần', 1),
       ('FW6', '1 tuần thăm 6 lần', 1),
       ('FW7', '1 tuần thăm 7 lần', 1),
       ('F2W', '2 tuần thăm 1 lần', 2),
       ('F3W', '3 tuần thăm 1 lần', 3),
       ('F4W', '4 tuần thăm 1 lần', 4);

insert into sales_route_config(days, repeat_week, visit_frequency_id)
values ('2, 4, 6', '1', 'FW3'),
       ('2, 4', '1', 'FW2'),
       ('3, 5', '1', 'FW2'),
       ('4, 6', '1', 'FW2'),
       ('4', '2', 'F2W'),
       ('5', '3', 'F3W'),
       ('6', '4', 'F4W');
