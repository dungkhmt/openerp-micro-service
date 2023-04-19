import { config } from "./constant";
const baseUrl = config.url.API_URL;
export const endPoint = {
  getAuthorization: baseUrl + "",
  getProductCategory: baseUrl + "/category/product-category/get-all-category",
};
