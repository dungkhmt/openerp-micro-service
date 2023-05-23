import { request } from "api"

export const getShipmentByCode = async (data) => {
    const resData = request(
        "get",
        `/shipment/${data}`, {},{}, {},{},
      )
    return resData;
}

export const createShipment = (data) => {
    const resData = request(
        "post",
        `/shipment/create`, {},{}, data, {},
    )
    return resData;
}