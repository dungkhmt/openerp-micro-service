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