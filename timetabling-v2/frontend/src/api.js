import axios from "axios";
import { config } from "config/constant";
import keycloak from "config/keycloak";
import { infoNoti, wifiOffNotify } from "./utils/notification";

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
 * @param {*} onSuccess: success handler
 * @param {*} onErrors: error handler
 * @param {*} data: body request
 * @param {*} config: using for form-data request
 * @param {optional} token: using for cancle request
 */
export async function request(
  method,
  url,
  successHandler,
  errorHandlers,
  data,
  config,
  controller
) {
  if (config !== undefined && config !== null) {
    axiosInstance.defaults.headers.common["Content-Type"] =
      "multipart/form-data";
  } else {
    axiosInstance.defaults.headers.common["Content-Type"] = "application/json";
  }
  try {
    let options = {};
    if (controller) {
      options = {
        method: method.toLowerCase(),
        url: url,
        data: data,
        signal: controller?.signal,
        ...config,
        headers: {
          authorization: bearerAuth(keycloak.token),
          ...config?.headers,
        },
      };
    } else {
      options = {
        method: method.toLowerCase(),
        url: url,
        data: data,
        ...config,
        headers: {
          authorization: bearerAuth(keycloak.token),
          ...config?.headers,
        },
      };
    }

    const res = await axiosInstance.request(options);

    if (isFunction(successHandler)) {
      successHandler(res);
    }
    return res;
  } catch (e) {
    // Handling work to do when encountering all kinds of errors, e.g turn off the loading icon.
    if (isFunction(errorHandlers)) {
      errorHandlers(e);
    }
    if (e.response) {
      // The request was made and the server responded with a status code that falls out of the range of 2xx.
      switch (e.response.status) {
        // case 401:
        //   if (isFunction(errorHandlers[401])) {
        //     errorHandlers[401](e);
        //   } else {
        //     history.push({ pathname: "/login" });
        //   }
        //   break;
        case 403:
          if (isFunction(errorHandlers[403])) {
            errorHandlers[403](e);
          } else {
            infoNoti("Bạn cần được cấp quyền để thực hiện hành động này.");
          }
          break;
        default:
          if (isFunction(errorHandlers[e.response.status])) {
            console.error(e);
            errorHandlers[e.response.status](e);
          } else if (isFunction(errorHandlers["rest"])) {
            errorHandlers["rest"](e);
          } else {
            // unduplicatedErrorNoti(
            //   "response error",
            //   "Rất tiếc! Đã có lỗi xảy ra."
            // );
          }
      }
    } else if (e.request) {
      // The request was made but no response was received
      // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
      // http.ClientRequest in node.js
      console.log(
        "The request was made but no response was received",
        e.request
      );

      if (isFunction(errorHandlers["noResponse"])) {
        errorHandlers["noResponse"](e);
      }

      // , "Không thể kết nối tới máy chủ."
      wifiOffNotify(wifiOffNotifyToastId);
    } else {
      // Something happened in setting up the request that triggered an Error.
      console.log(
        "Something happened in setting up the request that triggered an error: ",
        e.message
      );
    }
    console.log("Request config", e.config);
  }
}
