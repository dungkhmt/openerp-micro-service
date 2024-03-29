import { request } from "api";

export const getTrucks = async (data) => {
  const resData = request("post", `/truck/`, {}, {}, data, {});
  return resData;
};

export const getTruckById = async (data) => {
  const resData = request("get", `/truck/${data}`, {}, {}, {}, {});
  return resData;
};

export const createTruck = (data) => {
  const resData = request("post", `/truck/create`, {}, {}, data, {});
  return resData;
};

export const updateTruck = (id, data) => {
  const resData = request("put", `/truck/update/${id}`, {}, {}, data, {});
  return resData;
};

export const deleteTruck = (uid) => {
  const resData = request("delete", `/truck/delete/${uid}`, {}, {}, {}, {});
  return resData;
};

export const getAllDriver = () => {
  const resData = request(
    "get",
    `https://erp3.soict.ai/iam/admin/realms/OpenERP-Dev/roles/TMS_DRIVER/users?first=0&max=101`,
    {},
    {},
    {},
    {}
  );
  return resData;
};
