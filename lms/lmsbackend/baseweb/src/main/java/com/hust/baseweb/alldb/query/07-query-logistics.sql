-- lay danh sach cac shipment_item cua mot party nao do
select *
from shipment_item
where shipment_item_id in (select shipment_item_id
                           from shipment_item_role
                           where party_id = '287db6a8-2783-11ea-b1c9-54bf64436441'
                             and thru_date is null);

