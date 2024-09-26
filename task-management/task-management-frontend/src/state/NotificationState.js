import { hookstate, useHookstate } from "@hookstate/core";

export const notificationState = hookstate({
  open: false,
  notifications: undefined,
  numUnRead: 0,
  hasMore: false,
});

export function useNotificationState() {
  return useHookstate(notificationState);
}
