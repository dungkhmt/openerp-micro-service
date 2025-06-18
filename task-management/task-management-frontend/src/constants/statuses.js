export const TASK_STATUS_IDS = {
  active: "ASSIGNMENT_ACTIVE",
  inactive: "ASSIGNMENT_INACTIVE",
  inProgress: "TASK_INPROGRESS",
  open: "TASK_OPEN",
  resolved: "TASK_RESOLVED",
  closed: "TASK_CLOSED",
};
export const TASK_STATUS_LIST = Object.values(TASK_STATUS_IDS);

export const PLAN_STATUS_IDS = {
  draft: "PLAN_DRAFT",
  registrationOpen: "PLAN_REG_OPEN",
  registrationClosed: "PLAN_REG_CLOSED",
  assigned: "PLAN_ASSIGNED",
  inProgress: "PLAN_IN_PROGRESS",
  completed: "PLAN_COMPLETED",
  canceled: "PLAN_CANCELED",
};
export const PLAN_STATUS_LIST = Object.values(PLAN_STATUS_IDS);

export const INVITATION_STATUS_IDS = {
  pending: "ORG_PENDING",
  accepted: "ORG_ACCEPTED",
  declined: "ORG_DECLINED",
  expired: "ORG_EXPIRED",
};
export const INVITATION_STATUS_LIST = Object.values(INVITATION_STATUS_IDS);
