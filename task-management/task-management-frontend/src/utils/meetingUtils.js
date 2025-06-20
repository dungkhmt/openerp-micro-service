import dayjs from "dayjs";

export const isRegistrationMode = (statusId) =>
  ["PLAN_REG_OPEN", "PLAN_REG_CLOSED"].includes(statusId);

export const isRegistrationOpen = (statusId, deadline) =>
  statusId === "PLAN_REG_OPEN" && dayjs().isBefore(dayjs(deadline));
