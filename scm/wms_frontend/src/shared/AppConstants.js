import { AppColors } from "./AppColors";

export const ITEM_STATUS_COLOR_MAPPING = {
  active: AppColors.info,
  inactive: AppColors.warning,
  deleted: AppColors.error,
};
export const ORDER_STATUS_COLOR_MAPPING = {
  created: AppColors.pendingStatus,
  accepted: AppColors.approvedStatus,
  delivering: AppColors.warning,
  delivered: AppColors.green,
  deleted: AppColors.error,
};
export const ORDERS_STATUS = {
  created: "CREATED",
  accepted: "ACCEPTED",
  delivering: "DELIVERING",
  delivered: "DELIVERED",
  deleted: "DELETED",
};
