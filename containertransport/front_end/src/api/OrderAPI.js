import { request } from "api";

export const getOrders = async (data) => {
    const resData = request(
        "post",
        "/order/", {}, {}, data, {},
    );
    return resData;
}

export const createOrder = async (data) => {
    const resData = request(
        "post",
        `/order/create`, {}, {}, data
    );
    return resData;
}

export const getOrderByUid = async (data) => {
    const resData = request(
        "get",
        `/order/${data}`, {}, {}, {}, {}
    );
    return resData;
}

export const updateOrder = async (id, data) => {
    const resData = request(
        "put",
        `/order/update/${id}`, {}, {}, data, {}
    );
    return resData;
}

export const updateOrderByOrderCode = async (orderCode, data) => {
    const resData = request(
        "put",
        `/order/update/${orderCode}`, {}, {}, data, {}
    );
    return resData;
}