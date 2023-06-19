import { request } from "api"

export const getShipmentByCode = async (data) => {
    const resData = request(
        "get",
        `/shipment/${data}`, {},{}, {},{},
      )
    return resData;
}

export const getShipment = async (data) => {
    const resData = request(
        "post",
        "/shipment/", {}, {}, data, {},
    );
    return resData;
}

export const createShipment = (data) => {
    const resData = request(
        "post",
        `/shipment/create`, {},{}, data, {},
    )
    return resData;
}

export const updateShipment = (id, data) => {
    const resData = request(
        "put",
        `/shipment/update/${id}`, {},{}, data, {},
    )
    return resData;
}

export const autoCreateRouter = (data) => {
    const resData = request(
        "get",
        `/solution/${data}`, {},{}, {}, {},
    )
    return resData;
}