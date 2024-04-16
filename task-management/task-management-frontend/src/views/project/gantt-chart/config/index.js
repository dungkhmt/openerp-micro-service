import GSTC from "gantt-schedule-timeline-calendar/dist/gstc.wasm.esm.min.js";
import { Plugin as TimelinePointer } from "gantt-schedule-timeline-calendar/dist/plugins/timeline-pointer.esm.min.js";
import { Plugin as Selection } from "gantt-schedule-timeline-calendar/dist/plugins/selection.esm.min.js";
import { Plugin as ItemResizing } from "gantt-schedule-timeline-calendar/dist/plugins/item-resizing.esm.min.js";
import { Plugin as ItemMovement } from "gantt-schedule-timeline-calendar/dist/plugins/item-movement.esm.min.js";
import { Plugin as HighlightWeekends } from "gantt-schedule-timeline-calendar/dist/plugins/highlight-weekends.esm.min.js";
import { Plugin as ProgressBar } from "gantt-schedule-timeline-calendar/dist/plugins/progress-bar.esm.min.js";
import { Plugin as CalendarScroll } from "gantt-schedule-timeline-calendar/dist/plugins/calendar-scroll.esm.min.js";
import { Plugin as TimeBookmarks } from "gantt-schedule-timeline-calendar/dist/plugins/time-bookmarks.esm.min.js";
import { Plugin as DependencyLines } from "gantt-schedule-timeline-calendar/dist/plugins/dependency-lines.esm.min.js";
import { Plugin as ExportImage } from "gantt-schedule-timeline-calendar/dist/plugins/export-image.esm.min.js";
import { Plugin as ExportPDF } from "gantt-schedule-timeline-calendar/dist/plugins/export-pdf.esm.min.js";
import { getRandomColor } from "./color";
import { itemResizeOptions } from "./resize";
import { itemMovementOptions } from "./movement";
import { columns } from "./column";
import {
  addDays,
  chartTimelineItemsRowItemTemplate,
  endDate,
  itemSlot,
  myBirthdayRowSlot,
  myItemSlot,
  myVacationRowSlot,
  onCellCreateBirthday,
  onCellCreateVacation,
  onLevelDates,
  rowSlot,
  startDate,
} from "./lib";

/**
 * @type {import("gantt-schedule-timeline-calendar/dist/plugins/time-bookmarks").Bookmarks}
 */
const bookmarks = {};
for (let i = 0; i < 3; i++) {
  const id = `Bookmark ${i}`;
  bookmarks[id] = {
    time: startDate
      .add(Math.round(Math.random() * addDays), "day")
      .startOf("day")
      .valueOf(),
    label: id,
    style: {
      background: getRandomColor(),
    },
  };
}

/**
 *
 * @return {import('gantt-schedule-timeline-calendar/dist/gstc').Config}
 */
export const buildConfig = ({ listRows, chartItems }) => ({
  // ! The license key is for testing purposes only, will expire in 2 months
  // ! Please get your own license key from https://gstc.neuronet.io/free-key/
  licenseKey:
    "====BEGIN LICENSE KEY====\nLJ8TI6WxbTVIwld0JHpE7OC9TtrXzTEiwgFkflylqkeWP+QA4o0KZzphP5YQPryO8fqzmn0e7n8FV0+QDPwaLq6P7J+HPRTFvoJ8D9eZx5EHD/Dc3SZMxoxIXhzB4X9F3gQbM86mxwknEjfQOhGlWEwzhvbWoILB7cLWijtj0tnPqdRD0T4WTYukkfbWI3FHxjDZiYzlUbn+hOFWRHIJrVf0CbHepQARqO0P6o9wMYOWf1hn6RyymxuqHpYPcScfDm+me0DZHrjeGqhxIigOKXe25PDNEZhC1Wn2BEkFVB9IR6tmmfsF1bep8wQj7TFWu8A0BeZOWYeL4MomKFxteQ==||U2FsdGVkX1+dWz07FoUMIQpSGcvjOnzRN8YHhb4ZvDBI2qEEgV/FFvHo14YrP8Dza/yyY6+mSD+w3vViNNgllV5Pe3qHoOF6YNHYxI7PT7g=\nCpUN0koRph7sdZqZNafj74c6CqY0+qwHEjRy1T8VU/m8XWR9+KwLskpJEUykyqJuzgj3F6cyJ8a391Z9Sy8BqjIz8aMN8Ub72QSk0tM5gfnB60S3e0J+VpZUlt2ldsI+zBg2w1qzq77C2ObSggLelqJnYX9GR1V5b/RnffVnTXAWbVKB2ODC9QiyfvPJABFdq8u+vMV9E75oKT8WxkcSBGQ3vbQYN+Gxe+x7RMb1C62Y+r9vGzCXRUjWazBtvM+i7E3uO0fF0ISqZW/LJDx46Dt/q/8pxJuOAa2FbrHLcxxbG4Vue0mrl0RJXi/qZ2zMa/hLjvmh9pu16zYaDGr+7Q==\n====END LICENSE KEY====",
  // innerHeight: 900,
  autoInnerHeight: true,
  plugins: [
    HighlightWeekends(),
    TimelinePointer(), // timeline pointer must go first before selection, resizing and movement
    Selection({
      events: {
        onEnd(selected) {
          console.log("Selected", selected);
          return selected;
        },
      },
    }),
    ItemResizing(itemResizeOptions), // resizing must fo before movement
    ItemMovement(itemMovementOptions),
    CalendarScroll(),
    ProgressBar(),
    TimeBookmarks({
      bookmarks,
    }),
    DependencyLines({
      onLine: [
        (line) => {
          line.type =
            GSTC.api.sourceID(line.fromItem.id) === "3" ? "smooth" : "square";
          return line;
        },
      ],
    }),
    ExportImage(),
    ExportPDF(),
  ],
  list: {
    row: {
      height: 56,
    },
    rows: listRows,
    columns,
  },
  chart: {
    time: {
      from: startDate.valueOf(),
      to: endDate.valueOf(),
      onLevelDates: [onLevelDates],
    },
    item: {
      height: 50,
      gap: {
        top: 14,
        //bottom: 0,
      },
    },
    items: chartItems,
    grid: {
      cell: {
        onCreate: [onCellCreateVacation, onCellCreateBirthday],
      },
    },
  },
  scroll: {
    vertical: { precise: true, byPixels: true },
    horizontal: { precise: true, byPixels: true },
  },
  slots: {
    "chart-timeline-items-row-item": {
      content: [itemSlot],
      inner: [myItemSlot],
    },
    "list-column-row": { content: [rowSlot] },
    "chart-timeline-grid-row": {
      content: [myBirthdayRowSlot, myVacationRowSlot],
    },
  },
  templates: {
    "chart-timeline-items-row-item": chartTimelineItemsRowItemTemplate,
  },
  //utcMode: true,
});
