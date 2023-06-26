import { request } from "api"

export const getFacility = async (data) => {
    const resData = request(
        "post",
        `/facility/`, {},{}, data,{},
      )
    return resData;
}

export const getFacilityById = async (data) => {
    const resData = request(
        "get",
        `/facility/${data}`, {},{}, {}, {},
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

export const updateFacility = (id, data) => {
    const resData = request(
        "put",
        `/facility/update/${id}`, {},{}, data, {},
    )
    return resData;
}

export const deleteFacility = (id) => {
    const resData = request(
        "delete",
        `/facility/delete/${id}`, {},{}, {}, {},
    )
    return resData;
}