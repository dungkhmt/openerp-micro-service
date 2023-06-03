import { config } from "./constant";
const baseUrl = config.url.API_URL;
export const endPoint = {
  // Auth
  getAuthorization: baseUrl + "",
  // Category
  getProductCategory: baseUrl + "/category/product-category/get-all",
  deleteProductCategory: baseUrl + "/category/product-category/remove",

  getProductUnit: baseUrl + "/category/product-unit/get-all",
  deleteProductUnit: baseUrl + "/category/product-unit/remove",

  getDistChannel: baseUrl + "/category/distributing-channel/get-all",
  deleteDistChannel: baseUrl + "/category/distributing-channel/remove",

  getCustomerType: baseUrl + "/category/customer-type/get-all",
  deleteCustomerType: baseUrl + "/category/customer-type/remove",

  getContractType: baseUrl + "/category/contract-type/get-all",
  deleteContractType: baseUrl + "/category/customer-type/remove",

  getProduct: baseUrl + "/product/get-all",
  getProductNoPaging: baseUrl + "/product/get-all-without-paging",
  getCustomer: baseUrl + "/customer/get-all",
  getCustomerNoPaging: baseUrl + "/customer/get-all-without-paging",
  createCustomer: baseUrl + "/customer/create",
  deleteCustomer: baseUrl + "/customer/delete",
  createProduct: baseUrl + "/product/create",

  createProductUnit: baseUrl + "/category/product-unit/create",
  createProductCategory: baseUrl + "/category/product-category/create",
  createCustomerType: baseUrl + "/category/customer-type/create",
  createDistChannel: baseUrl + "/category/distributing-channel/create",
  createContractType: baseUrl + "/category/contract-type/create",
  importProduct: baseUrl + "/product/create-from-file",
  importCustomer: baseUrl + "/customer/create-from-file",
  // Purchase Order
  getPurchaseOrder: baseUrl + "/purchase-order/get-all",
  getPurchaseOrderItems: baseUrl + "/purchase-order/get-order-items",
  updatePurchaseOrderStatus: baseUrl + "/purchase-order/update-status",
  createPurchaseOrder: baseUrl + "/purchase-order/create",
  createSellinPrice: baseUrl + "/product/set-purchase-price",
  getSellinPrice: baseUrl + "/product/get-all-sellin-price",
  updateSellinPrice: baseUrl + "/product/sellin-price/update",
  // Facility
  getFacility: baseUrl + "/facility/get-all",
  getFacilityInventory: baseUrl + "/facility/get-inventory",
  createFacility: baseUrl + "/facility/create",
  getFacilityNoPaging: baseUrl + "/facility/get-all-without-paging",
  getFacilityCustomers: baseUrl + "/facility/get-customer-of-facility-paging",
  // Bill
  getReceiptBills: baseUrl + "/receipt-bill/get-all",
  getBillItemOfPurchaseOrder: baseUrl + "/receipt-bill/get-bill-items-of-order",
  getBillItemOfPurchaseOrderPaging:
    baseUrl + "/receipt-bill/get-bill-items-of-order-paging",
  createReceiptBill: baseUrl + "/facility/import-item",
  getDeliveryBills: baseUrl + "/delivery-bill/get-all",
  getBillItemOfSaleOrder: baseUrl + "/delivery-bill/get-bill-items-of-order",
  getBillItemOfBill: baseUrl + "/delivery-bill/get-bill-items-of-bill",
  createDeliveryBill: baseUrl + "/facility/export-item",
  getSplittedBillItems: baseUrl + "/delivery-bill/get-split-bill",
  createSplitBillItems: baseUrl + "/delivery-bill/split-bill",
  // Sale Order
  getSaleOrder: baseUrl + "/sale-order/get-all",
  getSaleOrderItems: baseUrl + "/sale-order/get-order-items",
  createSaleOrder: baseUrl + "/sale-order/create",
  updateSaleOrderStatus: baseUrl + "/sale-order/update-status",
  createSelloutPrice: baseUrl + "/product/set-sale-price",
  getSelloutPrice: baseUrl + "/product/get-all-sellout-price",
  updateSelloutPrice: baseUrl + "/product/sellout-price/update",
  // Shipment
  createShipment: baseUrl + "/shipment/create",
  getShipments: baseUrl + "/shipment/get-all",
  getShipmentItems: baseUrl + "/shipment/get-all-item",
  getItemOfTrip: baseUrl + "/shipment/get-item-of-trip",
  assignShipmentToTrip: baseUrl + "/shipment/assign-shipment-item",
  // Delivery
  createDeliveryTrip: baseUrl + "/delivery-trip/create",
  getDeliveryTrips: baseUrl + "/delivery-trip/get-all",
  getTripToAssignBill: baseUrl + "/delivery-trip/get-trip-to-assign",
  createTripRoute: baseUrl + "/delivery-trip/create-trip-route",
  deleteTripRoute: baseUrl + "/delivery-trip/delete-trip-route",
  getTruck: baseUrl + "/vehicle/truck/get-all",
  deleteTruck: baseUrl + "/vehicle/truck/remove",
  getDrone: baseUrl + "/vehicle/drone/get-all",
  deleteDrone: baseUrl + "/vehicle/drone/remove",
  createTruck: baseUrl + "/vehicle/truck/create",
  createDrone: baseUrl + "/vehicle/drone/create",
  getTripRoutes: baseUrl + "/delivery-trip/get-trip-route",
  // User
  // createNewUser: base
  getAllUsers: baseUrl + "/user/get-all",
  getAllUsersWithoutPagination: baseUrl + "/user/get-all-exists",
};
