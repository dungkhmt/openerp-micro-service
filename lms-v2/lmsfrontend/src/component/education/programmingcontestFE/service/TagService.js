import {request} from "../../../../api";

export const getAllTags = (successHandler) => {
  request(
    "get",
    "/tags/",
    successHandler,
    {}
  ).then();
}