-- check inventory_item quantity count có match với product_warehouse quantity không
select wii.warehouse_id, wii.product_id, sum(wii.quantity_on_hand_total) s
from wms_inventory_item wii
group by wii.warehouse_id, wii.product_id
having sum(wii.quantity_on_hand_total) != (select wpw.quantity_on_hand from wms_product_warehouse wpw where wpw.warehouse_id = wii.warehouse_id and wpw.product_id = wii.product_id);
