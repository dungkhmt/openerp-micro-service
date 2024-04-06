// check have exist value of object (object, array, string, number, boolean) with deep check
export const checkExistValue = (obj, val) => {
  if (obj === undefined || obj === null) {
    return false;
  }
  if (typeof obj === "object") {
    if (Array.isArray(obj)) {
      return obj.some((o) => checkExistValue(o, val));
    } else {
      return Object.values(obj).some((o) => checkExistValue(o, val));
    }
  } else {
    return obj === val;
  }
};
