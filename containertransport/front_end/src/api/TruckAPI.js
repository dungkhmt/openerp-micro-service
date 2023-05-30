import { request } from "api"

export const getTrucks = async (data) => {
    const resData = request(
        "post",
        `/truck/`, {},{}, data,{},
      )
    return resData;
}

export const createTruck = (data) => {
    const resData = request(
        "post",
        `/truck/create`, {},{}, data, {},
    )
    return resData;
}