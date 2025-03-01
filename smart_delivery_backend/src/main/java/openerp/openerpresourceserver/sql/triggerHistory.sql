-- auto-generated definition
create table smartdelivery_order_history
(
    id                     uuid not null,
    approved_by            varchar(255),
    cancelled_by           varchar(255),
    created_at             timestamp(6),
    created_by             varchar(255),
    expected_delivery_date timestamp(6),
    final_price            numeric(38, 2),
    ship_price             numeric(38, 2),
    total_price            numeric(38, 2),
    updated_at             timestamp(6),
    collector_id           uuid
        constraint fk4f16wfss4brpghc5gv5idsxk2
            references smartdelivery_collector,
    final_hub_id           uuid
        constraint fkq9wagaxrhdh4li3y77dx7hgqc
            references smartdelivery_hub,
    origin_hub_id          uuid
        constraint fkobfjgcxg7x9y9jvpcs50bbd9y
            references smartdelivery_hub,
    recipient_id           uuid
        constraint fkcgsq1d5bpl8xbgjd8ysjt10mb
            references smartdelivery_recipient,
    sender_id              uuid
        constraint fkbe5rycritktog1vfc2l0832ur
            references smartdelivery_sender,
    shipper_id             uuid
        constraint fkchx5w9cvtfoxg8uj7oqghook7
            references smartdelivery_shipper,
    order_type             varchar(255),
    shipping_price         numeric(38, 2),
    hub_id                 uuid
        constraint fkdx229hll78rcrmbelksyi5n8q
            references smartdelivery_hub,
    destination_address    varchar(255),
    origin                 varchar(255),
    distance               double precision,
    status                 varchar(255),
    collector_name         varchar(255),
    final_hub_name         varchar(255),
    origin_hub_name        varchar(255),
    recipient_name         varchar(255),
    sender_name            varchar(255),
    shipper_name           varchar(255),
    changed_at TIMESTAMP(6) NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (id, changed_at)  -- Đã thêm changed_at vào khóa chính

);

alter table smartdelivery_order_history
    owner to postgres;

CREATE OR REPLACE FUNCTION move_order_to_history()
    RETURNS TRIGGER AS $$
BEGIN
    -- Chèn dữ liệu cũ vào bảng history
INSERT INTO smartdelivery_order_history (
    id, approved_by, cancelled_by, created_at, created_by, expected_delivery_date,
    final_price, ship_price, total_price, updated_at, collector_id, final_hub_id,
    origin_hub_id, recipient_id, sender_id, shipper_id, order_type, shipping_price,
    hub_id, destination_address, origin, distance, status, collector_name,
    final_hub_name, origin_hub_name, recipient_name, sender_name, shipper_name,
    changed_at, changed_by
)
VALUES (
           OLD.id, OLD.approved_by, OLD.cancelled_by, OLD.created_at, OLD.created_by,
           OLD.expected_delivery_date, OLD.final_price, OLD.ship_price, OLD.total_price,
           OLD.updated_at, OLD.collector_id, OLD.final_hub_id, OLD.origin_hub_id,
           OLD.recipient_id, OLD.sender_id, OLD.shipper_id, OLD.order_type,
           OLD.shipping_price, OLD.hub_id, OLD.destination_address, OLD.origin,
           OLD.distance, OLD.status, OLD.collector_name, OLD.final_hub_name,
           OLD.origin_hub_name, OLD.recipient_name, OLD.sender_name, OLD.shipper_name,
           NOW(), OLD.created_by  -- Sử dụng NOW() để lấy thời gian hiện tại
       );

-- Trả về NEW để tiếp tục cập nhật bảng smartdelivery_order
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_move_order_to_history
    BEFORE UPDATE ON smartdelivery_order
    FOR EACH ROW
    WHEN (OLD.status IS DISTINCT FROM NEW.status OR
          OLD.collector_id IS DISTINCT FROM NEW.collector_id OR
          OLD.shipper_id IS DISTINCT FROM NEW.shipper_id)  -- Chỉ kích hoạt khi trạng thái thay đổi
EXECUTE FUNCTION move_order_to_history();