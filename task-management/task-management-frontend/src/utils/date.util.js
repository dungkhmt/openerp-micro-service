import dayjs from "dayjs";
import { INVITATION_STATUS_IDS } from "../constants/statuses";

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

export const getTimeUntilDeadline = (toDate) => {
  let diff = dayjs(toDate).diff(dayjs(), "seconds");

  if (diff <= 0) return "Đã hết hạn";

  const units = [
    { label: "năm", value: 31536000 },
    { label: "tháng", value: 2592000 },
    { label: "tuần", value: 604800 },
    { label: "ngày", value: 86400 },
    { label: "giờ", value: 3600 },
    { label: "phút", value: 60 },
    { label: "giây", value: 1 },
  ];

  let result = [];
  for (const unit of units) {
    const count = Math.floor(diff / unit.value);
    if (count > 0) {
      result.push(`${count} ${unit.label}`);
      diff -= count * unit.value;
    }
    if (result.length === 2) break; // Limit to 2 units (e.g., "1 ngày và 2 giờ")
  }

  return `Còn ${result.join(" và ")}`;
};

export const getInvitationExpirationText = (expirationTime, statusId) => {
  const expiration = dayjs(expirationTime);

  if (statusId === INVITATION_STATUS_IDS.expired) {
    return `Hết hạn vào ${expiration.format("D MMM, YYYY")}`;
  }

  if (statusId === INVITATION_STATUS_IDS.pending) {
    return getTimeUntilDeadline(expiration);
  }
  return "";
};