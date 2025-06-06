import axios from "axios";
import { config } from "./config/constant";
import keycloak from "./config/keycloak";
import { infoNoti } from "./utils/notification";

export const isFunction = (func) =>
  func &&
  (Object.prototype.toString.call(func) === "[object Function]" ||
    "function" === typeof func ||
    func instanceof Function);

const axiosInstance = axios.create({
  baseURL: config.url.API_URL,
});

// Alter defaults after instance has been created
axiosInstance.defaults.headers.common["Content-Type"] = "application/json";

const wifiOffNotifyToastId = "cannot connect to server";

export function bearerAuth(token) {
  return `Bearer ${token}`;
}

/**
 * url, method, and data properties don't need to be specified in config.
 * @param {*} method
 * @param {*} url
 * @param {*} onSuccess
 * @param {*} onErrors
 * @param {*} data
 * @param {*} config
 */
export async function request(
  method,
  url,
  successHandler,
  errorHandlers = {},
  data,
  config
) {
  try {
    // Check if the data is an instance of FormData
    // If yes, don't override the content type header, axios will set it automatically
    const headers = {
      authorization: bearerAuth(keycloak.token),
      ...config?.headers,
    };

    // If data is FormData, don't manually set the content type
    if (data instanceof FormData) {
      delete headers["Content-Type"]; // Remove Content-Type so axios can handle it
    } else {
      headers["Content-Type"] = "application/json"; // Set Content-Type for JSON data
    }

    const res = await axiosInstance.request({
      method: method.toLowerCase(),
      url: url,
      data: data,
      ...config,
      headers: headers,
    });

    if (isFunction(successHandler)) {
      successHandler(res);
    }
    return res;
  } catch (e) {
    // Handle errors
    if (isFunction(errorHandlers["onError"])) {
      errorHandlers["onError"](e);
    }

    if (e.response) {
      switch (e.response.status) {
        case 403:
          if (isFunction(errorHandlers[403])) {
            errorHandlers[403](e);
          } else {
            infoNoti("Bạn cần được cấp quyền để thực hiện hành động này.");
          }
          break;
        default:
          if (isFunction(errorHandlers[e.response.status])) {
            errorHandlers[e.response.status](e);
          } else if (isFunction(errorHandlers["rest"])) {
            errorHandlers["rest"](e);
          } else {
            // Handle default error
          }
      }
    } else if (e.request) {
      console.log("The request was made but no response was received", e.request);
      if (isFunction(errorHandlers["noResponse"])) {
        errorHandlers["noResponse"](e);
      }
      // wifiOffNotify(wifiOffNotifyToastId);
    } else {
      console.log("Something happened in setting up the request: ", e.message);
    }
    console.log("Request config", e.config);
  }
}

