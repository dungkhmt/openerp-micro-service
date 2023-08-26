import { request } from "api";

export const getRateTruck = async () => {
    const resData = request(
        "post",
        "/dashboard/truck-rate", {}, {}, {}, {},
    );
    return resData;
}

export const getRateTrailer = async () => {
    const resData = request(
        "post",
        "/dashboard/trailer-rate", {}, {}, {}, {},
    );
    return resData;
}

export const getOrderByMonth = async (data) => {
    const resData = request(
        "get",
        `/dashboard/order-by-month?year=${data}`, {}, {}, {}, {},
    );
    return resData;
}

export const getRateUseContainer = async () => {
    const resData = request(
        "post",
        `/dashboard/type-container-rate`, {}, {}, {}, {},
    );
    return resData;
}