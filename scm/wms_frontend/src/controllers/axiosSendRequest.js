import axios from "axios";
// import curlirize from 'axios-curlirize';
import { toast } from "react-toastify";
import keycloak from "../config/keycloak";
import { AppConfig } from "../shared/AppConfig";
const qs = require("qs");
const tag = "axiosSendRequest";

export function bearerAuth(token) {
  return `Bearer ${token}`;
}
/**
 * @author hoang
 * @param {'get' | 'put' | 'post' | 'delete' | 'patch'} method
 * @param {string} url
 * @param {object} params
 * @param {string} object
 * @return {Promise<{data: object, statusCode: number, message: string}>}
 */
async function axiosSendRequest(
  method,
  url,
  params = null,
  data = null,
  header = null
) {
  if (AppConfig.isDebugging) {
    console.log(
      "\n[Axios " + method + "]: \n\t url = ",
      url,
      "\n\t params = ",
      JSON.stringify(params),
      "\n\t data = ",
      JSON.stringify(data),
      "\n\t header = ",
      header
    );
  }

  // kiểm tra kết nối mạng
  // if (await checkInternetConnection()) {
  // } else {
  //   handleRequestError({
  //     result: "error",
  //     message: "no_internet",
  //   });

  //   return {
  //     result: "error",
  //     message: "no_internet",
  //   };
  // }
  let responseData = {
    result: "error",
    message: "Không xác định",
    code: 0,
  };
  // required
  const config = {
    method: method,
    url: url,
  };
  config.headers = {
    authorization: bearerAuth(keycloak.token),
    ...config?.headers,
  };
  // optional
  if (header) {
    config.headers = { ...config.headers, header };
  }
  // optional
  if (params) {
    if (method === "get" || method === "put" || method === "delete") {
      config.url =
        url +
        "?" +
        qs.stringify(params, {
          arrayFormat: "brackets",
          encode: false,
        });
    }
  }
  if (data) {
    config.data = data || {};
  }

  // curlirize(axios);
  await axios(config)
    .catch(function (error) {
      if (AppConfig.isDebugging) {
        console.log("Error: ", error, error.response);
      }
      if (error.response) {
        responseData.code = error.response.status;
        responseData.message = error.response.data?.errors?.[0]
          ? error.response.data?.errors?.[0]
          : error.response.data?.error;
      }
      if (AppConfig.isDebugging) {
        console.warn("[Axios]", error);
      }
      return { data: responseData };
    })
    .then((response) => response.data)
    .then(handleRequestError)
    .then((data) => {
      responseData = data;
    });
  if (AppConfig.isDebugging) {
    console.log(
      "\n[Axios Response Data]: \n\t",
      url,
      "\n\t",
      responseData,
      "\n"
    );
  }
  return responseData;
}

export const handleRequestError = (data) => {
  const { result, message, code } = data;
  if (code !== 1) {
    toast.error(message);
  }
  if (result === "error" && message === "no_internet") {
    toast.error(
      "Không có internet hoặc mạng không ổn định, vui lòng kiểm tra lại"
    );
  }
  if (code === 401) {
    // toast.error("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", {
    // });
    // toast.dismiss();
  }
  return data;
};

/**
 * set a field for all axios request header
 * @param {*} key
 * @param {*} value
 */
export const setAxiosHeader = (key, value) => {
  axios.defaults.headers.common[key] = value;
  console.log("[Axios]", "Set default header", { key, value });
};

// setAxiosHeader('Accept-Encoding', 'gzip');
setAxiosHeader("Content-Type", "application/json");
// setAxiosHeader("Accept", "application/json");
export default axiosSendRequest;
