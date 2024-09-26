export const LimitString = (limit, string) => {
  if (string.length > limit) {
    return string.substring(0, limit) + "...";
  } else {
    return string;
  }
};
