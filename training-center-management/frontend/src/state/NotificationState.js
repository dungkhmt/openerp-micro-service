import { createState, useState } from "@hookstate/core";

export const notificationState = createState({
  open: false,
  notifications: undefined,
  numUnRead: 0,
  hasMore: false,
});

export function useNotificationState() {
  return useState(notificationState);
}
