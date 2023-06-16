import { request } from "api";

export const getContainers = async (data) => {
    const resData = request(
        "post",
        "/container/", {}, {}, data, {},
    );
    return resData;
}

export const getContainerById = async (data) => {
    const resData = request(
        "get",
        `/container/${data}`, {}, {}, {}, {},
    );
    return resData;
}

export const createContainer = async (data) => {
    const resData = request(
        "post",
        "/container/create", {}, {}, data, {},
    );
    return resData;
}
export const getTypeContainer = async (data) => {
    const resData = request(
        "post",
        "/type/container/", {}, {}, data, {},
    );
    return resData;
}

export const updateContainer = async (data) => {
    const resData = request(
        "put",
        "/container/update", {}, {}, data, {},
    );
    return resData;
}