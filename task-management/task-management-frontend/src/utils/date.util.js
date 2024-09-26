import dayjs from "dayjs";

export const getDiffDateWithCurrent = (from) => {
  // return  [2, seconds] or [2, minutes], [3, hours], [4, days], [5, weeks], [6, months], [7, years]
  const diff = dayjs().diff(from, "seconds");

  if (diff < 60) {
    return [diff, "giây"];
  }

  if (diff < 3600) {
    return [Math.floor(diff / 60), "phút"];
  }

  if (diff < 86400) {
    return [Math.floor(diff / 3600), "giờ"];
  }

  if (diff < 604800) {
    return [Math.floor(diff / 86400), "ngày"];
  }

  if (diff < 2592000) {
    return [Math.floor(diff / 604800), "tuần"];
  }

  if (diff < 31536000) {
    return [Math.floor(diff / 2592000), "tháng"];
  }

  return [Math.floor(diff / 31536000), "năm"];
};
