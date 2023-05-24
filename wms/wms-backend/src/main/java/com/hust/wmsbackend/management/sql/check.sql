-- check inventory_item quantity count có match với product_warehouse quantity không
select wii.warehouse_id, wii.product_id, sum(wii.quantity_on_hand_total) s
from wms_inventory_item wii
group by wii.warehouse_id, wii.product_id
having sum(wii.quantity_on_hand_total) != (select wpw.quantity_on_hand from wms_product_warehouse wpw where wpw.warehouse_id = wii.warehouse_id and wpw.product_id = wii.product_id);

select wri.product_id, ww.warehouse_id, sum(wri.quantity)
from wms_receipt_item wri
         join wms_bay wb on wri.bay_id = wb.bay_id
         join wms_warehouse ww on wb.warehouse_id = ww.warehouse_id
group by wri.product_id, ww.warehouse_id
having sum(wri.quantity) != (select wpw.quantity_on_hand from wms_product_warehouse wpw where wpw.warehouse_id = ww.warehouse_id and wpw.product_id = wri.product_id);

select wri.product_id, ww.warehouse_id, sum(wri.quantity)
from wms_receipt_item wri
         join wms_bay wb on wri.bay_id = wb.bay_id
         join wms_warehouse ww on wb.warehouse_id = ww.warehouse_id
group by wri.product_id, ww.warehouse_id
having sum(wri.quantity) != (select sum(wii.quantity_on_hand_total) from wms_inventory_item wii where wii.product_id = wri.product_id and wii.warehouse_id = ww.warehouse_id group by wii.warehouse_id, wii.product_id);
