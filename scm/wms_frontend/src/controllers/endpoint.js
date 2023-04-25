import { config } from "./constant";
const baseUrl = config.url.API_URL;
export const endPoint = {
  // Auth
  getAuthorization: baseUrl + "",
  // Category
  getProductCategory: baseUrl + "/category/product-category/get-all",
  getProductUnit: baseUrl + "/category/product-unit/get-all",
  getDistChannel: baseUrl + "/category/distributing-channel/get-all",
  getCustomerType: baseUrl + "/category/customer-type/get-all",
  getContractType: baseUrl + "/category/contract-type/get-all",
  getProduct: baseUrl + "/product/get-all",
  getCustomer: baseUrl + "/customer/get-all",
  createCustomer: baseUrl + "/customer/create",
  createProduct: baseUrl + "/product/create",
  // Purchase Order
  getPurchaseOrder: baseUrl + "/purchase-order/get-all",
  getPurchaseOrderItems: baseUrl + "/purchase-order/get-order-items",
  updatePurchaseOrderStatus: baseUrl + "/purchase-order/update-status",
  createPurchaseOrder: baseUrl + "/purchase-order/create",
  // Facility
  getFacility: baseUrl + "/facility/get-all",
  getFacilityInventory: baseUrl + "/facility/get-inventory",
  // Bill
  getReceiptBills: baseUrl + "/receipt-bill/get-all",
  getBillItemOfPurchaseOrder: baseUrl + "/receipt-bill/get-bill-items",
  createReceiptBill: baseUrl + "/facility/import-item",
  getDeliveryBills: baseUrl + "/delivery-bill/get-all",
  getBillItemOfSaleOrder: baseUrl + "/delivery-bill/get-bill-items",
  createDeliveryBill: baseUrl + "/facility/export-item",
  // Sale Order
  getSaleOrder: baseUrl + "/sale-order/get-all",
  getSaleOrderItems: baseUrl + "/sale-order/get-order-items",
  createSaleOrder: baseUrl + "/sale-order/create",
  updateSaleOrderStatus: baseUrl + "/sale-order/update-status",
  // Shipment
  createShipment: baseUrl + "/shipment/create",
};
