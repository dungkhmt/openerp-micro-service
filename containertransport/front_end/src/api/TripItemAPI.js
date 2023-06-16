import { request } from "api"

export const getTripItemByTripId = async (data) => {
    const resData = request(
        "post",
        `/tripItem/${data}`, {},{}, {},{},
      )
    return resData;
}

export const updateTripItem = (id, data) => {
  const resData = request(
      "put",
      `/tripItem/update/${id}`, {},{}, data, {},
  )
  return resData;
}