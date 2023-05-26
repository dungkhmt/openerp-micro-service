import { request } from "api"

export const getTripItemByTripId = async (data) => {
    const resData = request(
        "post",
        `/tripItem/${data}`, {},{}, {},{},
      )
    return resData;
}