import { canMove, snapStart } from "./lib";

/**
 * @type {import('gantt-schedule-timeline-calendar/dist/plugins/item-movement').Options}
 */
export const itemMovementOptions = {
  threshold: {
    horizontal: 25,
    vertical: 25,
  },
  snapToTime: {
    start: snapStart,
    end({ endTime }) {
      return endTime;
    },
  },
  events: {
    onMove({ items }) {
      for (let i = 0, len = items.after.length; i < len; i++) {
        const item = items.after[i];
        if (!canMove(item)) return items.before;
      }
      return items.after;
    },
  },
};
