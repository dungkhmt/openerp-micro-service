import {request} from "../../../../api";

export const getAllTags = (successHandler) => {
  request(
    "get",
    "/tags/",
    successHandler,
    {}
  ).then();
}

export const addNewTag = (body, successHandler, callback) => {
  request(
    "post",
    "/tags/",
    successHandler,
    {},
    body
  ).then(callback);
}