-- Function for Order Audit (only for UPDATE and DELETE)
CREATE OR REPLACE FUNCTION order_audit_trigger_func()
    RETURNS TRIGGER AS $$
BEGIN
    -- For UPDATE operations
    IF (TG_OP = 'UPDATE') THEN
        -- Save the OLD state with its current version to history
        INSERT INTO smartdelivery_order_history (
            id, order_id, status, version, changed_by,
            sender_id, sender_name, recipient_id, recipient_name, order_type,
            total_price, shipping_price, final_price, origin, destination_address,
            expected_delivery_date, origin_hub_id, origin_hub_name, final_hub_id, final_hub_name,
            original_created_at, original_created_by, created_at
        ) VALUES (
                     gen_random_uuid(), OLD.id, OLD.status, OLD.version, OLD.changed_by,
                     OLD.sender_id, OLD.sender_name, OLD.recipient_id, OLD.recipient_name, OLD.order_type,
                     OLD.total_price, OLD.shipping_price, OLD.final_price, OLD.origin, OLD.destination_address,
                     OLD.expected_delivery_date, OLD.origin_hub_id, OLD.origin_hub_name, OLD.final_hub_id, OLD.final_hub_name,
                     OLD.created_at, OLD.created_by, NOW()
                 );

RETURN NEW;

-- For DELETE operations
ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO smartdelivery_order_history (
            id, order_id, status, version, changed_by,
            sender_id, sender_name, recipient_id, recipient_name, order_type,
            total_price, shipping_price, final_price, origin, destination_address,
            expected_delivery_date, origin_hub_id, origin_hub_name, final_hub_id, final_hub_name,
            original_created_at, original_created_by, created_at, change_reason
        ) VALUES (
                     gen_random_uuid(), OLD.id, OLD.status, OLD.version, OLD.changed_by,
                     OLD.sender_id, OLD.sender_name, OLD.recipient_id, OLD.recipient_name, OLD.order_type,
                     OLD.total_price, OLD.shipping_price, OLD.final_price, OLD.origin, OLD.destination_address,
                     OLD.expected_delivery_date, OLD.origin_hub_id, OLD.origin_hub_name, OLD.final_hub_id, OLD.final_hub_name,
                     OLD.created_at, OLD.created_by, NOW()
                 );
RETURN OLD;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;


-- Function for OrderItem Audit (only for UPDATE and DELETE)
CREATE OR REPLACE FUNCTION order_item_audit_trigger_func()
    RETURNS TRIGGER AS $$
BEGIN
    -- For UPDATE operations
    IF (TG_OP = 'UPDATE') THEN
        -- Save the OLD state with its current version to history
        INSERT INTO smartdelivery_order_item_history (
            id, order_item_id, order_id, name, quantity, weight, price,
            length, width, height, status, version, changed_by, created_at
        ) VALUES (
                     gen_random_uuid(), OLD.order_item_id, OLD.order_id, OLD.name, OLD.quantity, OLD.weight, OLD.price,
                     OLD.length, OLD.width, OLD.height, OLD.status, OLD.version, OLD.updated_by, NOW()
                 );

RETURN NEW;

-- For DELETE operations
ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO smartdelivery_order_item_history (
            id, order_item_id, order_id, name, quantity, weight, price,
            length, width, height, status, version, changed_by, created_at
        ) VALUES (
                     gen_random_uuid(), OLD.order_item_id, OLD.order_id, OLD.name, OLD.quantity, OLD.weight, OLD.price,
                     OLD.length, OLD.width, OLD.height, OLD.status, OLD.version, OLD.updated_by, NOW()
                 );
RETURN OLD;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for Order (only for UPDATE and DELETE)
DROP TRIGGER IF EXISTS order_audit_trigger ON smartdelivery_order;

CREATE TRIGGER order_audit_trigger
    AFTER UPDATE OR DELETE ON smartdelivery_order
    FOR EACH ROW EXECUTE FUNCTION order_audit_trigger_func();

-- Create triggers for OrderItem (only for UPDATE and DELETE)
DROP TRIGGER IF EXISTS order_item_audit_trigger ON smartdelivery_order_item;

CREATE TRIGGER order_item_audit_trigger
    AFTER UPDATE OR DELETE ON smartdelivery_order_item
    FOR EACH ROW EXECUTE FUNCTION order_item_audit_trigger_func();

                           -- Function for Trip Audit (UPDATE and DELETE)
CREATE OR REPLACE FUNCTION trip_audit_trigger_func()
    RETURNS TRIGGER AS $$
DECLARE
driver_name_var VARCHAR(255);
BEGIN


    -- For UPDATE operations
    IF (TG_OP = 'UPDATE') THEN
        -- Save the OLD state to history
        INSERT INTO smartdelivery_trip_history (
            id, trip_id, changed_by, created_at,
            status, current_stop_index,
            driver_id, version, notes,
            orders_picked_up
        ) VALUES (
                     gen_random_uuid(), OLD.id,
                     OLD.changed_by,
                     NOW(),
                     OLD.status, OLD.current_stop_index,
                     OLD.driver_id, OLD.version,
                     CASE
                         WHEN OLD.status != NEW.status
                             THEN 'Status changed from ' || COALESCE(OLD.status, 'null') || ' to ' || COALESCE(NEW.status, 'null')
                         WHEN OLD.current_stop_index != NEW.current_stop_index
                             THEN 'Stop index changed from ' || COALESCE(OLD.current_stop_index::text, 'null') || ' to ' || COALESCE(NEW.current_stop_index::text, 'null')
                         ELSE 'Trip updated'
                         END,
                     OLD.orders_picked_up
                 );

RETURN NEW;

-- For DELETE operations
ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO smartdelivery_trip_history (
            id, trip_id, changed_by, created_at,
            status, current_stop_index,
            driver_id, version, notes,
            orders_picked_up
        ) VALUES (
                     gen_random_uuid(), OLD.id,
                     'system',
                     NOW(),
                     OLD.status, OLD.current_stop_index,
                     OLD.driver_id, OLD.version,
                     'Trip deleted',
                     OLD.orders_picked_up
                 );

RETURN OLD;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for Trip (UPDATE and DELETE)
DROP TRIGGER IF EXISTS trip_audit_trigger ON smartdelivery_trip;

CREATE TRIGGER trip_audit_trigger
    AFTER UPDATE OR DELETE ON smartdelivery_trip
    FOR EACH ROW EXECUTE FUNCTION trip_audit_trigger_func();