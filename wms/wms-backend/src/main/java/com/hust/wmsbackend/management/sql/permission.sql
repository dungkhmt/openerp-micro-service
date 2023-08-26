-- Permission for Warehouse manager role (thủ kho)
    -- Hiển thị danh sách menu
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_ADMIN.WAREHOUSE', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_ADMIN.PRODUCT', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_ADMIN.ORDER', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_ADMIN.PROCESS_RECEIPT', 'WMS_WAREHOUSE_MANAGER', null, now(), null);

    -- xem màn hình danh sách kho
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_WAREHOUSE_LISTING.VIEW', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
    -- xem màn hình kho chi tiết
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_WAREHOUSE.VIEW', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
    -- xem màn hình danh sách sản phẩm
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_PRODUCT_LISTING.VIEW', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
    -- xem màn hình sản phẩm chi tiết
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_PRODUCT.VIEW', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
    -- xem màn hình danh sách đơn xuất hàng cần xử lý, đã xử lý
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ORDER_LISTING.VIEW', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
    -- Xem màn hình chi tiết đơn xuất hàng chưa xử lý
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ORDER_DETAIl.VIEW', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
    -- Xem màn hình danh sách đơn nhập hàng cần xử lý
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_RECEIPT_REQUEST_PROCESS_LISTING.VIEW', 'WMS_WAREHOUSE_MANAGER', null, now(), null);
    -- xem màn hình phiếu nhập hàng chi tiết
insert into entity_authorization (id, role_id, last_updated, created, description)
values ('SCR_WMSv2_RECEIPT_BILL_DETAIL.VIEW', 'WMS_WAREHOUSE_MANAGER', null, now(), null) ;
    -- xem màn hình phiếu xuất hàng chi tiết
insert into entity_authorization (id, role_id, last_updated, created, description)
values ('SCR_WMSv2_DELIVERY_BILL_DETAIL.VIEW', 'WMS_WAREHOUSE_MANAGER', null, now(), null) ;

-- permission cho giám đốc doanh nghiệp
    -- xem danh sách menu
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_APPROVER.RECEIPTS', 'WMS_PRESIDENT', null, now(), null);

    -- Xem màn hình danh sách đơn xin nhập hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_RECEIPT_REQUEST_FOR_APPROVER.VIEW', 'WMS_PRESIDENT', null, now(), null);

    -- Xem màn hình Report
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_APPROVER.REPORT', 'WMS_PRESIDENT', null, now(), null);

-- permission cho giám đốc kinh doanh
    -- xem danh sách menu
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_SALE_MANAGER.PRICE_CONFIG', 'WMS_BUSINESS_MANAGER', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_SALE_MANAGER.RECEIPT_REQUEST', 'WMS_BUSINESS_MANAGER', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_SALE_MANAGER.ORDERS', 'WMS_BUSINESS_MANAGER', null, now(), null);
    -- xem màn hình cấu hình giá bán
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_PRICE_CONFIG.VIEW', 'WMS_BUSINESS_MANAGER', null, now(), null);
    -- Xem màn hình tạo đơn xin nhập hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_CREATE_RECEIPT_REQUEST.VIEW', 'WMS_BUSINESS_MANAGER', null, now(), null);
    -- Xem màn hình danh sách đơn mua hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_SALE_ORDER_LISTING.VIEW', 'WMS_BUSINESS_MANAGER', null, now(), null);

-- permission cho quản lý giao hàng
    -- xem danh sách menu
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_PERSON', 'WMS_DELIVERY_MANAGER', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.SHIPMENTS', 'WMS_DELIVERY_MANAGER', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_TRIPS', 'WMS_DELIVERY_MANAGER', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.ITEMSS', 'WMS_DELIVERY_MANAGER', null, now(), null);
    -- Xem màn hình quản lý nhân viên giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_DELIVERY_PERSON_MANAGEMENT.VIEW', 'WMS_DELIVERY_MANAGER', null, now(), null);
    -- Xem màn hình danh sách các đợt giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_SHIPMENT_LISTING.VIEW', 'WMS_DELIVERY_MANAGER', null, now(), null);
    -- Xem màn hình chi tiết của đợt giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_SHIPMENT_DETAIL.VIEW', 'WMS_DELIVERY_MANAGER', null, now(), null);
    -- Xem màn hình quản lý danh sách các chuyến giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_DELIVERY_TRIP_LISTING.VIEW', 'WMS_DELIVERY_MANAGER', null, now(), null);
    -- Xem màn hình chi tiết chuyến giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_DELIVERY_TRIP_DETAIL.VIEW', 'WMS_DELIVERY_MANAGER', null, now(), null);

-- permission cho nhân viên giao hàng
    -- xem danh sách menu
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_PERSON.DELIVERY_TRIP', 'WMS_DELIVERY_PERSON', null, now(), null);
    -- Xem danh sách các chuyến giao hàng hôm nay
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_TODAY_DELIVERY_TRIP.VIEW', 'WMS_DELIVERY_PERSON', null, now(), null);
    -- Xem danh sách chi tiết chuyến giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_TODAY_DELIVERY_TRIP_DETAIL.VIEW', 'WMS_DELIVERY_PERSON', null, now(), null);

-- permission cho khách hàng online
    -- xem danh sách menu
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_CUSTOMER.PRODUCTS', 'WMS_ONLINE_CUSTOMER', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_CUSTOMER.CART', 'WMS_ONLINE_CUSTOMER', null, now(), null);
    -- Xem danh sách sản phẩm
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ONLINE_CUSTOMER_PRODUCT_GENERAL.VIEW', 'WMS_ONLINE_CUSTOMER', null, now(), null);
    -- Xem sản phẩm chi tiết
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ONLINE_CUSTOMER_PRODUCT_DETAIL.VIEW', 'WMS_ONLINE_CUSTOMER', null, now(), null);
    -- Xem giỏ hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ONLINE_CUSTOMER_CART.VIEW', 'WMS_ONLINE_CUSTOMER', null, now(), null);

-- permission xem màn hình cho ADMIN
-- xem danh sách menu
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_ADMIN.WAREHOUSE', 'ADMIN', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_ADMIN.PRODUCT', 'ADMIN', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_ADMIN.ORDER', 'ADMIN', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_ADMIN.PROCESS_RECEIPT', 'ADMIN', null, now(), null);

INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_APPROVER.RECEIPTS', 'ADMIN', null, now(), null);

INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_SALE_MANAGER.PRICE_CONFIG', 'ADMIN', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_SALE_MANAGER.RECEIPT_REQUEST', 'ADMIN', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_SALE_MANAGER.ORDERS', 'ADMIN', null, now(), null);

INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_PERSON', 'ADMIN', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.SHIPMENTS', 'ADMIN', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.DELIVERY_TRIPS', 'ADMIN', null, now(), null);

INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_PERSON.DELIVERY_TRIP', 'ADMIN', null, now(), null);

INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_CUSTOMER.PRODUCTS', 'ADMIN', null, now(), null);
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_CUSTOMER.CART', 'ADMIN', null, now(), null);

INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_DELIVERY_MANAGER.ITEMS', 'ADMIN', null, now(), null);
-- xem màn hình danh sách kho
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_WAREHOUSE_LISTING.VIEW', 'ADMIN', null, now(), null);
-- xem màn hình kho chi tiết
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_WAREHOUSE.VIEW', 'ADMIN', null, now(), null);
-- xem màn hình danh sách sản phẩm
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_PRODUCT_LISTING.VIEW', 'ADMIN', null, now(), null);
-- xem màn hình sản phẩm chi tiết
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_PRODUCT.VIEW', 'ADMIN', null, now(), null);
-- xem màn hình danh sách đơn xuất hàng cần xử lý, đã xử lý
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ORDER_LISTING.VIEW', 'ADMIN', null, now(), null);
-- Xem màn hình chi tiết đơn xuất hàng chưa xử lý
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ORDER_DETAIl.VIEW', 'ADMIN', null, now(), null);
-- Xem màn hình danh sách đơn nhập hàng cần xử lý
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_RECEIPT_REQUEST_PROCESS_LISTING.VIEW', 'ADMIN', null, now(), null);

-- Xem màn hình danh sách đơn xin nhập hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_RECEIPT_REQUEST_FOR_APPROVER.VIEW', 'ADMIN', null, now(), null);

-- xem màn hình cấu hình giá bán
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_PRICE_CONFIG.VIEW', 'ADMIN', null, now(), null);
-- Xem màn hình tạo đơn xin nhập hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_CREATE_RECEIPT_REQUEST.VIEW', 'ADMIN', null, now(), null);
-- Xem màn hình danh sách đơn mua hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_SALE_ORDER_LISTING.VIEW', 'ADMIN', null, now(), null);

-- Xem màn hình quản lý nhân viên giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_DELIVERY_PERSON_MANAGEMENT.VIEW', 'ADMIN', null, now(), null);
-- Xem màn hình danh sách các đợt giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_SHIPMENT_LISTING.VIEW', 'ADMIN', null, now(), null);
-- Xem màn hình chi tiết của đợt giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_SHIPMENT_DETAIL.VIEW', 'ADMIN', null, now(), null);
-- Xem màn hình quản lý danh sách các chuyến giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_DELIVERY_TRIP_LISTING.VIEW', 'ADMIN', null, now(), null);
-- Xem màn hình chi tiết chuyến giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_DELIVERY_TRIP_DETAIL.VIEW', 'ADMIN', null, now(), null);

-- Xem danh sách các chuyến giao hàng hôm nay
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_TODAY_DELIVERY_TRIP.VIEW', 'ADMIN', null, now(), null);
-- Xem danh sách chi tiết chuyến giao hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_TODAY_DELIVERY_TRIP_DETAIL.VIEW', 'ADMIN', null, now(), null);

-- Xem danh sách sản phẩm
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ONLINE_CUSTOMER_PRODUCT_GENERAL.VIEW', 'ADMIN', null, now(), null);
-- Xem sản phẩm chi tiết
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ONLINE_CUSTOMER_PRODUCT_DETAIL.VIEW', 'ADMIN', null, now(), null);
-- Xem giỏ hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_ONLINE_CUSTOMER_CART.VIEW', 'ADMIN', null, now(), null);

-- xem màn hình chi tiết phê duyệt đơn xin  nhập hàng
insert into entity_authorization (id, role_id, last_updated, created, description)
values ('SCR_WMSv2_RECEIPT_REQUEST_DETAIL_FOR_APPROVER.VIEW', 'ADMIN', null, now(), null) ;

-- xem màn hình xử lý đơn nhập hàng
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_RECEIPT_REQUEST_PROCESS.VIEW', 'ADMIN', null, now(), null);

-- xem màn hình report
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_APPROVER.REPORT', 'ADMIN', null, now(), null);

-- xem màn hình phiếu nhập hàng chi tiết
insert into entity_authorization (id, role_id, last_updated, created, description)
values ('SCR_WMSv2_RECEIPT_BILL_DETAIL.VIEW', 'ADMIN', null, now(), null) ;

-- xem màn hình phiếu xuất hàng chi tiết
insert into entity_authorization (id, role_id, last_updated, created, description)
values ('SCR_WMSv2_DELIVERY_BILL_DETAIL.VIEW', 'ADMIN', null, now(), null) ;

-- bổ sung thêm use case cho phép giám đốc mua hàng tạo đơn mua hàng trực tiếp mà không cần thông qua đơn yêu cầu nhập hàng của giám đốc kinh doanh
INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_PURCHASE_MANAGER.CREATE_RECEIPTS', 'ADMIN', now(), now(), null);

INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('MENU_WMSv2_PURCHASE_MANAGER.CREATE_RECEIPTS', 'WMS_PURCHASE_MANAGER', now(), now(), null);

INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_CREATE_RECEIPT_REQUEST_FOR_PURCHASE_MANAGER.VIEW', 'ADMIN', now(), now(), null);

INSERT INTO public.entity_authorization
(id, role_id, last_updated, created, description)
VALUES('SCR_WMSv2_CREATE_RECEIPT_REQUEST_FOR_PURCHASE_MANAGER.VIEW', 'WMS_PURCHASE_MANAGER', now(), now(), null);