import { request } from "api"

export const getFacility = async (data) => {
    const resData = request(
        "post",
        `/facility/`, {},{}, data,{},
      )
    return resData;
}

export const createFacility = (data) => {
    const resData = request(
        "post",
        `/facility/create`, {},{}, data, {},
    )
    return resData;
}