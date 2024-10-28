import {request} from "../../../../api";

export const saveProblemToContest = (body, successHandler, callback) => {
  request(
    "post",
    "/contest-problem/",
    successHandler,
    {},
    body
  ).then(callback);
}