import {errorNoti, infoNoti, wifiOffNotify} from "../../../utils/notification";
import axios from "axios";
import {API_URL} from "../../../config/config";
import {store} from "../../../index";
import history from "../../../history";

const wifiOffNotifyToastId = "cannot connect to server";

const isFunction = (func) =>
  func &&
  (Object.prototype.toString.call(func) === "[object Function]" ||
    "function" === typeof func ||
    func instanceof Function);

const axiosInstance = axios.create({
  baseURL: API_URL,
});
export async function request(
  method,
  url,
  successHandler,
  errorHandlers = {},
  data,
  config
) {
  try {
    const res = await axiosInstance.request({
      method: method.toLowerCase(),
      url: url,
      data: data,
      ...config,
      headers: {
        "X-Auth-Token": store.getState().auth.token,
        ...config?.headers,
      },
    });
    if (isFunction(successHandler)) {
      successHandler(res);
    }
  } catch (e) {

    // Handling work to do when encountering all kinds of errors, e.g turn off the loading icon.
    if (isFunction(errorHandlers["onError"])) {
      errorHandlers["onError"](e);
    }

    if (e.response) {
      // The request was made and the server responded with a status code that falls out of the range of 2xx.
      console.log("err response ", e.response);
      switch (e.response.status) {
        case 401:
          if (isFunction(errorHandlers[401])) {
            errorHandlers[401](e);
          } else {
            history.push({ pathname: "/login" });
          }
          break;
        case 403:
          if (isFunction(errorHandlers[403])) {
            errorHandlers[403](e);
          } else {
            infoNoti("Bạn cần được cấp quyền để thực hiện hành động này.");
          }
          break;
        case 400:
          if (isFunction(errorHandlers[400])) {
            errorHandlers[400](e);
          } else {
            e.response.data.message != undefined? errorNoti(e.response.data.message, true) : errorNoti("Bad Request");
          }
          break;
        case 500:
          if (isFunction(errorHandlers[500])) {
            errorHandlers[500](e);
          } else {
            e.response.data.message != undefined? errorNoti(e.response.data.message, true) : errorNoti("Something is wrong in server")
          }
          break;
        default:
          if (isFunction(errorHandlers[e.response.status])) {
            errorHandlers[e.response.status](e);
          } else if (isFunction(errorHandlers["rest"])) {
            errorHandlers["rest"](e);
          } else {
          }
          break;
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
    // console.log("Request config", e.config);
  }
}
