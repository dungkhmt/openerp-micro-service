import { canMove, snapEnd, snapStart } from "./lib";

/**
 * @type {import('gantt-schedule-timeline-calendar/dist/plugins/item-resizing').Options}
 */
export const itemResizeOptions = {
  threshold: 25,
  snapToTime: {
    start: snapStart,
    end: snapEnd,
  },
  events: {
    onResize({ items }) {
      for (const item of items.after) {
        if (!canMove(item)) return items.before;
      }
      return items.after;
    },
  },
};
