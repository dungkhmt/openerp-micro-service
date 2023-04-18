select oi.ORDER_ID, oi.ORDER_ITEM_SEQ_ID, oi.PRODUCT_ID, pp.PRODUCT_PROMO_ID, pp.PROMO_NAME, ppr.RULE_NAME, ppc.PRODUCT_PROMO_COND_SEQ_ID, 
ppc.COND_VALUE, ppa.PRODUCT_PROMO_ACTION_SEQ_ID, ppp.PRODUCT_PROMO_ACTION_SEQ_ID,ppa.QUANTITY,ppa.AMOUNT, ppc.PRODUCT_PROMO_COND_SEQ_ID, ppp.PRODUCT_PROMO_COND_SEQ_ID 
from product_promo_product as ppp, order_item as oi, product_promo as pp, product_promo_rule as ppr, product_promo_action as ppa, product_promo_cond as ppc 
where ppp.product_id = '8931000005226'
and pp.THRU_DATE is null
and ppp.PRODUCT_PROMO_COND_SEQ_ID = ppc.PRODUCT_PROMO_COND_SEQ_ID
and oi.PRODUCT_ID = ppp.PRODUCT_ID 
and ppp.PRODUCT_PROMO_ID = ppa.PRODUCT_PROMO_ID 
and ppp.PRODUCT_PROMO_COND_SEQ_ID = ppc.PRODUCT_PROMO_COND_SEQ_ID
and (pp.PRODUCT_PROMO_ID = '9020' or pp.PRODUCT_PROMO_ID = '9010')
and ppa.AMOUNT is not null
and oi.ORDER_ID = 'ORDMB120440'
;

select oi.ORDER_ID, oi.ORDER_ITEM_SEQ_ID, oi.PRODUCT_ID, oa.ORDER_ADJUSTMENT_TYPE_ID,  oa.PRODUCT_PROMO_ID, pp.PRODUCT_PROMO_TYPE_ID,
oa.PRODUCT_PROMO_RULE_ID,
oa.PRODUCT_PROMO_ACTION_SEQ_ID, oa.AMOUNT, oa.DESCRIPTION, ppa.AMOUNT, ppa.QUANTITY
from order_adjustment as oa, order_item as oi, product_promo_action as ppa, product_promo as pp 
where oa.ORDER_ID = oi.ORDER_ID and oa.ORDER_ITEM_SEQ_ID = oi.ORDER_ITEM_SEQ_ID 
and ppa.PRODUCT_PROMO_ID = pp.PRODUCT_PROMO_ID
and oa.PRODUCT_PROMO_ID = ppa.PRODUCT_PROMO_ID and oa.PRODUCT_PROMO_ACTION_SEQ_ID = ppa.PRODUCT_PROMO_ACTION_SEQ_ID  
and oi.PRODUCT_ID is not null
and oi.PRODUCT_ID = '8931000005226'
and (oa.PRODUCT_PROMO_ID = '9020' or oa.PRODUCT_PROMO_ID = '9010')
order by oi.ORDER_ID

select oa.ORDER_ID, oa.ORDER_ITEM_SEQ_ID, tarp.TAX_AUTHORITY_RATE_SEQ_ID, tarp.TAX_PERCENTAGE from order_adjustment as oa, tax_authority_rate_product as tarp  
where oa.ORDER_ADJUSTMENT_TYPE_ID = 'SALES_TAX'
and tarp.TAX_PERCENTAGE > 5
;

select * from product_promo_cond;
select * from product_promo_rule;
select * from product_promo_action;

select * from enumeration where ENUM_ID = 'PROMO_GWP';
select * from party where party_id = "VNM_TAX";
select * from tax_authority_rate_type;
select * from tax_authority;
select * from tax_authority_rate_product;