import {ERROR} from "../action/error";

const error = (state = { isError: false, statusCode: null }, action) => {
  switch (action.type) {
    case ERROR:
      return Object.assign({}, state, {
        isError: true,
        statusCode: action.statusCode,
      });

    default:
      return state;
  }
};

export default error;
