import { request } from "api"

export const getTrips = async (data) => {
    const resData = request(
        "post",
        `/trip/`, {},{}, data,{},
      )
    return resData;
}

export const createTrip = (data) => {
    const resData = request(
        "post",
        `/trip/create`, {},{}, data, {},
    )
    return resData;
}

export const getTripByTripId = async (data) => {
    const resData = request(
        "post",
        `/trip/${data}`, {},{}, {}, {},
    )
    return resData;
}

export const getTripByDriver = async (data) => {
    const resData = request(
        "post",
        `/trip/get-by-driver`, {},{}, data, {},
    )
    return resData;
}

export const updateTrip = async (id, data) => {
    const resData = request(
        "put",
        `/trip/update/${id}`, {},{}, data, {},
    )
    return resData;
}

export const deleteTrip = async (data) => {
    const resData = request(
        "delete",
        `/trip/delete`, {},{}, data, {},
    )
    return resData;
}