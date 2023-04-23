import { config } from "./constant";
const baseUrl = config.url.API_URL;
export const endPoint = {
  getAuthorization: baseUrl + "",
  getProductCategory: baseUrl + "/category/product-category/get-all",
  getProductUnit: baseUrl + "/category/product-unit/get-all",
  getDistChannel: baseUrl + "/category/distributing-channel/get-all",
  getCustomerType: baseUrl + "/category/customer-type/get-all",
  getContractType: baseUrl + "/category/contract-type/get-all",
  getProduct: baseUrl + "/product/get-all",
  getCustomer: baseUrl + "/customer/get-all",
  createProduct: baseUrl + "/product/create",
  getPurchaseOrder: baseUrl + "/purchase-order/get-all",
  getPurchaseOrderItems: baseUrl + "/purchase-order/get-order-items",
  getFacility: baseUrl + "/facility/get-all",
  getFacilityInventory: baseUrl + "/facility/get-inventory",
  createPurchaseOrder: baseUrl + "/purchase-order/create",
  getReceiptBills: baseUrl + "/receipt-bill/get-all",
  getBillItemOfPurchaseOrder: baseUrl + "/receipt-bill/get-bill-items",
  createBill: baseUrl + "/facility/import-item",
};
