import { request } from "api"

export const getTraler = async (data) => {
    const resData = request(
        "post",
        `/trailer/`, {},{}, data,{},
      )
    return resData;
}

export const createTrailer = (data) => {
    const resData = request(
        "post",
        `/trailer/create`, {},{}, data, {},
    )
    return resData;
}

export const updateTrailer = (data) => {
    const resData = request(
        "put",
        `/trailer/update`, {},{}, data, {},
    )
    return resData;
}