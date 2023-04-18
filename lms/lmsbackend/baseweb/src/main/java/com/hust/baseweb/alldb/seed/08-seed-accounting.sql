insert into invoice_type(invoice_type_id, description)
values ('SALES_INVOICE', 'Hoá đơn bán hàng');

insert into invoice_item_type(invoice_item_type_id, description)
values ('SALES_INVOICE_PRODUCT_ITEM', 'Doanh thu bán hàng hóa sản phẩm');

insert into payment_type(payment_type_id, description)
values ('CUSTOMER_PAYMENT', 'Khách hàng thanh toán');
insert into payment_type(payment_type_id, description)
values ('COMPANY_PAYMENT', 'Công ty thanh toán');

insert into payment_method(payment_method_id, description)
values ('CASH', 'Tiền mặt');
insert into payment_method(payment_method_id, description)
values ('BANK', 'Chuyển khoản');
