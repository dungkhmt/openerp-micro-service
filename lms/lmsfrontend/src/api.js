import axios from "axios";
import {failed, logout} from "./action/Auth";
import {API_URL} from "./config/config";
import history from "./history";
import {store} from "./index";
import {authState} from "./state/AuthState";
import {infoNoti, wifiOffNotify} from "./utils/notification";

export const authPost = (dispatch, token, url, body) => {
  return fetch(API_URL + url, {
    method: "POST",
    headers: {
      "content-type": "application/json",
      "X-Auth-Token": token,
    },
    body: JSON.stringify(body),
  }).then(
    (res) => {
      if (!res.ok) {
        if (res.status === 401) {
          dispatch(failed());
          throw Error("Unauthorized");
        } else {
          console.log(res);
          try {
            res.json().then((res1) => console.log(res1));
          } catch (err) {}
          throw Error();
        }
        // return null;
      }
      return res.json();
    },
    (error) => {
      console.log(error);
    }
  );
};
export const authPostMultiPart = (dispatch, token, url, body) => {
  /*
  return fetch(API_URL + url, {
    method: "POST",
    headers: {
      "X-Auth-Token": token,
    },
    body: body,
  });
  */

  return fetch(API_URL + url, {
    method: "POST",
    headers: {
      "X-Auth-Token": token,
    },
    body: body,
  }).then(
    (res) => {
      if (!res.ok) {
        if (res.status === 401) {
          dispatch(failed());
          throw Error("Unauthorized");
        } else {
          console.log(res);
          throw Error();
        }
        // return null;
      }
      return res.json();
    },
    (error) => {
      console.log(error);
    }
  );
};
export const authPut = (dispatch, token, url, body) => {
  return fetch(API_URL + url, {
    method: "PUT",
    headers: {
      "content-type": "application/json",
      "X-Auth-Token": token,
    },
    body: JSON.stringify(body),
  });
};
export const authGetImg = (dispatch, token, url) => {
  return fetch(API_URL + url, {
    method: "GET",
    headers: {
      "X-Auth-Token": token,
      "content-type": "application/octet-stream",
    },
  });
};
export const authGet = (dispatch, token, url) => {
  return fetch(API_URL + url, {
    method: "GET",
    headers: {
      "content-type": "application/json",
      "X-Auth-Token": token,
    },
  }).then(
    (res) => {
      if (!res.ok) {
        if (res.status === 401) {
          dispatch(failed());
          throw Error("Unauthorized");
        } else {
          console.log(res);
          try {
            res.json().then((res1) => console.log(res1));
          } catch (err) {}
          throw Error();
        }
        // return null;
      }
      return res.json();
    },
    (error) => {
      console.log(error);
    }
  );
};

export const authGetBody = (dispatch, token, url, body) => {
  debugger;
  return fetch(API_URL + url, {
    method: "GET",
    headers: {
      "content-type": "application/json",
      "X-Auth-Token": token,
    },
    body: body,
  }).then(
    (res) => {
      if (!res.ok) {
        if (res.status === 401) {
          dispatch(failed());
          throw Error("Unauthorized");
        } else {
          console.log(res);
          try {
            res.json().then((res1) => console.log(res1));
          } catch (err) {}
          throw Error();
        }
        // return null;
      }
      return res.json();
    },
    (error) => {
      console.log(error);
    }
  );
};
export const authDelete = (dispatch, token, url, body) => {
  return fetch(API_URL + url, {
    method: "DELETE",
    headers: {
      "content-type": "application/json",
      "X-Auth-Token": token,
    },
    body: JSON.stringify(body),
  }).then(
    (res) => {
      if (!res.ok) {
        if (res.status === 401) {
          dispatch(failed());
          throw Error("Unauthorized");
        } else {
          console.log(res);
          try {
            res.json().then((res1) => console.log(res1));
          } catch (err) {}
          throw Error();
        }
        // return null;
      }
      return res.json();
    },
    (error) => {
      console.log(error);
    }
  );
};

export const axiosPost = (token, url, data, dispatch) => {
  return axios.post(API_URL + url, data, {
    headers: {
      "content-type": "application/json",
      "X-Auth-Token": token,
    },
  });
};

export const axiosPatch = (token, url, data, dispatch) => {
  return axios.patch(API_URL + url, data, {
    headers: {
      "content-type": "application/json",
      "X-Auth-Token": token,
    },
  });
};

export const axiosGet = (token, url, dispatch) => {
  return axios.get(API_URL + url, {
    headers: {
      "content-type": "application/json",
      "X-Auth-Token": token,
    },
  });
};

export const axiosPut = (token, url, data, dispatch) => {
  return axios.put(API_URL + url, data, {
    headers: {
      "content-type": "application/json",
      "X-Auth-Token": token,
    },
  });
};

export const isFunction = (func) =>
  func &&
  (Object.prototype.toString.call(func) === "[object Function]" ||
    "function" === typeof func ||
    func instanceof Function);

const axiosInstance = axios.create({
  baseURL: API_URL,
});

// Alter defaults after instance has been created
axiosInstance.defaults.headers.common["Content-Type"] = "application/json";

const wifiOffNotifyToastId = "cannot connect to server";

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
    const res = await axiosInstance.request({
      method: method.toLowerCase(),
      url: url,
      data: data,
      ...config,
      headers: {
        "X-Auth-Token": authState.token.get(),
        ...config?.headers,
      },
    });

    if (isFunction(successHandler)) {
      successHandler(res);
    }
    return res;
  } catch (e) {
    // Handling work to do when encountering all kinds of errors, e.g turn off the loading icon.
    if (isFunction(errorHandlers["onError"])) {
      errorHandlers["onError"](e);
    }

    if (e.response) {
      // The request was made and the server responded with a status code that falls out of the range of 2xx.
      switch (e.response.status) {
        case 401:
          if (isFunction(errorHandlers[401])) {
            errorHandlers[401](e);
          } else {
            history.push({ pathname: "/login" });
            store.dispatch(logout());
          }
          break;
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
