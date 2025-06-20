import dayjs from "dayjs";
import {
  INVITATION_STATUS_IDS,
  PLAN_STATUS_IDS,
  TASK_STATUS_IDS,
} from "../constants/statuses";

export const getCategoryColor = (category) => {
  switch (category) {
    case "TASK":
      return "success";
    case "REQUEST":
      return "warning";
    case "BUG":
      return "error";
    case "OTHER":
      return "info";
    case "IMPROVEMENT":
      return "primary";
    default:
      return "secondary";
  }
};

export const getStatusColor = (status) => {
  switch (status) {
    // Task statuses
    case TASK_STATUS_IDS.active:
      return "success";
    case TASK_STATUS_IDS.inactive:
      return "secondary";
    case TASK_STATUS_IDS.inProgress:
      return "primary";
    case TASK_STATUS_IDS.open:
      return "warning";
    case TASK_STATUS_IDS.resolved:
      return "success";
    case TASK_STATUS_IDS.closed:
      return "error";

    // Meeting plan statuses
    case PLAN_STATUS_IDS.draft:
      return "secondary";
    case PLAN_STATUS_IDS.registrationOpen:
      return "warning";
    case PLAN_STATUS_IDS.registrationClosed:
      return "info";
    case PLAN_STATUS_IDS.assigned:
      return "success";
    case PLAN_STATUS_IDS.inProgress:
      return "primary";
    case PLAN_STATUS_IDS.completed:
      return "success";
    case PLAN_STATUS_IDS.canceled:
      return "error";

    // Invitation statuses
    case INVITATION_STATUS_IDS.pending:
      return "warning";
    case INVITATION_STATUS_IDS.accepted:
      return "success";
    case INVITATION_STATUS_IDS.declined:
      return "error";
    case INVITATION_STATUS_IDS.expired:
      return "secondary";

    default:
      return "info";
  }
};

export const getPriorityColor = (priority) => {
  switch (priority) {
    case "LOW":
      return "success";
    case "NORMAL":
      return "info";
    case "HIGH":
      return "warning";
    case "URGENT":
      return "error";
    default:
      return "primary";
  }
};

export const getRandomColorSkin = (id = "") => {
  const colors = [
    "primary",
    "secondary",
    "success",
    "warning",
    "error",
    "info",
  ];

  // get a unique number from string id
  const index = id
    .split("")
    .map((char) => char.charCodeAt(0))
    .reduce((acc, cur) => acc + cur, 0);

  if (index) return colors[index % colors.length];
  return colors[Math.floor(Math.random() * colors.length)];
};

export const getDueDateColor = (dueDate) => {
  if (!dueDate) return "text.primary";

  const now = dayjs();
  const due = dayjs(dueDate);

  if (due.isBefore(now)) return "error.main";
  if (due.isBefore(now.add(3, "day"))) return "warning.main";
  if (due.isBefore(now.add(7, "day"))) return "info.main";
  return "text.primary";
};

export const getLogItemColor = (details) => {
  if (!details || details.length <= 0) return "info";

  if (details?.some((detail) => detail.event === "set")) return "success";

  if (details?.some((detail) => detail.event === "update")) {
    if (
      details?.some(
        (detail) =>
          detail.field === "statusId" && detail.newValue === "TASK_CLOSED"
      )
    )
      return "error";
    if (
      details?.some(
        (detail) =>
          detail.field === "statusId" && detail.newValue === "TASK_RESOLVED"
      )
    )
      return "primary";

    return "secondary";
  }

  if (details?.some((detail) => detail.event === "assign")) return "warning";

  return "info";
};

export const getProgressColor = (progress) => {
  if (progress < 30) return "error";
  if (progress < 70) return "warning";
  if (progress < 90) return "success";
  return "info";
};

export const getDeadlineColor = (deadline) => {
  if (!deadline) return "gray";

  const now = dayjs();
  const deadlineTime = dayjs(deadline);

  if (now.isAfter(deadlineTime)) return "error.main";
  if (deadlineTime.diff(now, "hour") < 24) return "warning.main";
  return "success.main";
};
