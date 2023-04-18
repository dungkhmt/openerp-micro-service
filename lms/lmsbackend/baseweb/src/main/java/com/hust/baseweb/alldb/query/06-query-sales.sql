-- lay ra danh sach khach hang (id,ten, dia chi, toa do), bang party_customer
select c.party_id, c.customer_name, a.address, g.latitude, g.longitude
from party_customer as c,
     postal_address as a,
     geo_point as g,
     party_contact_mech_purpose as pcmp
where a.geo_point_id = g.geo_point_id
  and pcmp.contact_mech_id = a.contact_mech_id
  and pcmp.party_id = c.party_id;

-- lay ra danh sach dai li ban le (id,ten, dia chi, toa do), bang party_retail_outlet
select ro.party_id, ro.retail_outlet_name, a.address, g.latitude, g.longitude
from party_retail_outlet as ro,
     postal_address as a,
     geo_point as g,
     party_contact_mech_purpose as pcmp
where a.geo_point_id = g.geo_point_id
  and pcmp.contact_mech_id = a.contact_mech_id
  and pcmp.party_id = ro.party_id;


-- lay ra danh sach nha phan phoi (id,ten, dia chi, toa do), bang party_distributor
select d.party_id, d.distributor_name, a.address, g.latitude, g.longitude
from party_distributor as d,
     postal_address as a,
     geo_point as g,
     party_contact_mech_purpose as pcmp
where a.geo_point_id = g.geo_point_id
  and pcmp.contact_mech_id = a.contact_mech_id
  and pcmp.party_id = d.party_id;


-- lay danh sach nhan vien ban hang (bang party_salesman)
select sm.party_id, p.first_name, p.middle_name, p.last_name, u.user_login_id
from party_salesman as sm,
     person as p,
     user_login as u
where sm.party_id = p.party_id
  and u.party_id = sm.party_id;

-- lay ra danh sach dai li ban le duoc phu trach boi 1 salesman nao do
select ro.retail_outlet_name
from party_relationship as r,
     user_login as u,
     party_retail_outlet as ro
where role_type_id = 'SALESMAN_SELL_TO_RETAILOUTLET'
  and thru_date is null
  and r.from_party_id = u.party_id
  and ro.party_id = r.to_party_id
  and u.user_login_id = 'dungpq';

--lay ra danh sach nha phan phoi duoc phu trach boi 1 salesman nao do
select d.distributor_name
from party_relationship as r,
     user_login as u,
     party_distributor as d
where role_type_id = 'SALESMAN_SELL_FROM_DISTRIBUTOR'
  and thru_date is null
  and r.from_party_id = u.party_id
  and d.party_id = r.to_party_id
  and u.user_login_id = 'dungpq';

--lay ra danh sach dai li ban le va nhan vien ban hang tuong ung cua mot NPP nao do (bang retail_outlet_salesman_vendor)
select ro.retail_outlet_name, u.user_login_id, d.distributor_name
from retail_outlet_salesman_vendor as rsv,
     party_salesman as sm,
     party_retail_outlet as ro,
     party_distributor as d,
     user_login as u
where u.party_id = sm.party_id
  and sm.party_id = rsv.party_salesman_id
  and ro.party_id = rsv.party_retail_outlet_id
  and rsv.party_vendor_id = d.party_id
  and d.distributor_code = 'NPP009';

--lay ra danh sach nha phan phoi va nhan vien ban hang tuong ung cua 1 dai li ban le nao do (bang retail_outlet_salesman_vendor)
select ro.retail_outlet_name, u.user_login_id, d.distributor_name
from retail_outlet_salesman_vendor as rsv,
     party_salesman as sm,
     party_retail_outlet as ro,
     party_distributor as d,
     user_login as u
where u.party_id = sm.party_id
  and sm.party_id = rsv.party_salesman_id
  and ro.party_id = rsv.party_retail_outlet_id
  and rsv.party_vendor_id = d.party_id
  and ro.retail_outlet_code = 'DLBL0008';

--lay ra danh sach dai li ban le va nha phan phoi tuong ung cua 1 nhan vien ban hang nao do (bang retail_outlet_salesman_vendor)
select ro.retail_outlet_name, u.user_login_id, d.distributor_name
from retail_outlet_salesman_vendor as rsv,
     party_salesman as sm,
     party_retail_outlet as ro,
     party_distributor as d,
     user_login as u
where u.party_id = sm.party_id
  and sm.party_id = rsv.party_salesman_id
  and ro.party_id = rsv.party_retail_outlet_id
  and rsv.party_vendor_id = d.party_id
  and u.user_login_id = 'dungpq';


--lay danh sach san pham (bang product)
select *
from product;

--lay danh sach don hang ban tu NPP den dai li ban le (party_retail_outlet), sap xep theo order_date
select o.order_id,
       o.order_date,
       oi.order_item_seq_id,
       p.product_name,
       oi.quantity,
       o.grand_total,
       oi.unit_price,
       ro.retail_outlet_name,
       ol1.role_type_id,
       u.user_login_id,
       ol2.role_type_id
from order_header as o,
     order_item as oi,
     product as p,
     order_role as ol1,
     order_role as ol2,
     party_retail_outlet as ro,
     party_salesman as sm,
     user_login as u
where o.order_id = oi.order_id
  and oi.product_id = p.product_id
  and o.order_id = ol1.order_id
  and o.order_id = ol2.order_id
  and ol1.party_id = ro.party_id
  and ol2.party_id = sm.party_id
  and sm.party_id = u.party_id
order by (o.order_date, o.order_id, oi.order_item_seq_id) desc;

-- lay danh sach don hang ban tu cong ty den NPP (party_distributor)
select o.order_id,
       o.order_date,
       oi.order_item_seq_id,
       p.product_name,
       oi.quantity,
       o.grand_total,
       oi.unit_price,
       d.distributor_name,
       ol1.role_type_id,
       u.user_login_id,
       ol2.role_type_id
from order_header as o,
     order_item as oi,
     product as p,
     order_role as ol1,
     order_role as ol2,
     party_distributor as d,
     party_salesman as sm,
     user_login as u
where o.order_id = oi.order_id
  and oi.product_id = p.product_id
  and o.order_id = ol1.order_id
  and o.order_id = ol2.order_id
  and ol1.party_id = d.party_id
  and ol2.party_id = sm.party_id
  and sm.party_id = u.party_id
order by (o.order_date, o.order_id, oi.order_item_seq_id) desc;


--lay danh sach cau hinh vieng tham
select srp.sales_route_planning_period_id,
       srp.description,
       srp.from_date,
       srp.to_date,
       u.party_id,
       u.user_login_id,
       ro.retail_outlet_name,
       d.distributor_name,
       src.days,
       src.repeat_week,
       vf.description
from sales_route_config_retail_outlet as srcro,
     retail_outlet_salesman_vendor as rosv,
     sales_route_config as src,
     party_salesman as sm,
     party_distributor as d,
     party_retail_outlet as ro,
     user_login as u,
     sales_route_visit_frequency as vf,
     sales_route_planning_period as srp
where srp.sales_route_planning_period_id = srcro.sales_route_planning_period_id
  and srcro.retail_outlet_salesman_vendor_id = rosv.retail_outlet_salesman_vendor_id
  and rosv.party_retail_outlet_id = ro.party_id
  and rosv.party_salesman_id = sm.party_id
  and rosv.party_vendor_id = d.party_id
  and u.party_id = sm.party_id
  and vf.visit_frequency_id = src.visit_frequency_id;

-- truy van thong tin tuyen vieng tham chi tiet (bang sales_route_detail)
select u.user_login_id, srd.execute_date, src.days, src.repeat_week, ro.retail_outlet_name, d.distributor_name
from sales_route_detail as srd,
     party_salesman as sm,
     party_distributor as d,
     party_retail_outlet as ro,
     user_login as u,
     sales_route_config as src,
     sales_route_config_retail_outlet as srcro
where u.party_id = sm.party_id
  and srd.party_retail_outlet_id = ro.party_id
  and srd.party_salesman_id = sm.party_id
  and srd.party_distributor_id = d.party_id
  and src.sales_route_config_id = srcro.sales_route_config_id
  and srd.sales_route_config_retail_outlet_id = srcro.sales_route_config_retail_outlet_id
order by (u.user_login_id, srd.execute_date, d.distributor_name);
