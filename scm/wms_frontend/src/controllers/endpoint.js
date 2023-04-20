import { config } from "./constant";
const baseUrl = config.url.API_URL;
export const endPoint = {
  getAuthorization: baseUrl + "",
  getProductCategory: baseUrl + "/category/product-category/get-all-category",
  getProductUnit: baseUrl + "/category/product-unit/get-all-unit",
  getDistChannel: baseUrl + "/category/distributing-channel/get-all",
  getCustomerType: baseUrl + "/category/customer-type/get-all",
  getContractType: baseUrl + "/category/contract-type/get-all",
  getProduct: baseUrl + "/product/get-all-product",
  getCustomer: baseUrl + "/customer/get-all-customer",
  createProduct: baseUrl + "/product/create",
};
