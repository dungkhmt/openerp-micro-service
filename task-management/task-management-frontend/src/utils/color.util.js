import dayjs from "dayjs";

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
    case "ASSIGNMENT_ACTIVE":
      return "success";
    case "ASSIGNMENT_INACTIVE":
      return "secondary";
    case "TASK_INPROGRESS":
      return "primary";
    case "TASK_OPEN":
      return "warning";
    case "TASK_RESOLVED":
      return "success";
    case "TASK_CLOSED":
      return "error";

    // Meeting plan statuses
    case "PLAN_DRAFT":
      return "secondary";
    case "PLAN_REG_OPEN":
      return "warning";
    case "PLAN_REG_CLOSED":
      return "info";
    case "PLAN_ASSIGNED":
      return "success";
    case "PLAN_IN_PROGRESS":
      return "primary";
    case "PLAN_COMPLETED":
      return "success";
    case "PLAN_CANCELED":
      return "error";

    // Fallback for unknown statuses
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
