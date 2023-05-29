import { request } from "api";

export const getContainers = async (data) => {
    const resData = request(
        "post",
        "/container/", {}, {}, data, {},
    );
    return resData;
}