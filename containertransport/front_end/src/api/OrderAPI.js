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

export const updateOrderV2 = async (uid, data) => {
    const resData = request(
        "put",
        `/order/updateV2/${uid}`, {}, {}, data, {}
    );
    return resData;
}

export const deleteOrder = async (uid, data) => {
    const resData = request(
        "delete",
        `/order/delete/${uid}`, {}, {}, data, {}
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

export const updateOrderList = async (data) => {
    const resData = request(
        "put",
        `/order/update/`, {}, {}, data, {}
    );
    return resData;
}