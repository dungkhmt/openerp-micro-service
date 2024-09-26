export const ERROR = "ERROR";

export const error = (statusCode) => {
  return {
    type: ERROR,
    statusCode: statusCode,
  };
};
