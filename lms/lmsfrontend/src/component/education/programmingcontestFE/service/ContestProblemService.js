import {request} from "../../../../api";

export const saveProblemToContest = (body, successHandler, callback) => {
  request(
    "post",
    "/save-problem-to-contest/",
    successHandler,
    {},
    body
  ).then(callback);
}