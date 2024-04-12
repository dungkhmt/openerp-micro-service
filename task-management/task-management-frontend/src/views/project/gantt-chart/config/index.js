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
  licenseKey:
    "====BEGIN LICENSE KEY====\nXOfH/lnVASM6et4Co473t9jPIvhmQ/l0X3Ewog30VudX6GVkOB0n3oDx42NtADJ8HjYrhfXKSNu5EMRb5KzCLvMt/pu7xugjbvpyI1glE7Ha6E5VZwRpb4AC8T1KBF67FKAgaI7YFeOtPFROSCKrW5la38jbE5fo+q2N6wAfEti8la2ie6/7U2V+SdJPqkm/mLY/JBHdvDHoUduwe4zgqBUYLTNUgX6aKdlhpZPuHfj2SMeB/tcTJfH48rN1mgGkNkAT9ovROwI7ReLrdlHrHmJ1UwZZnAfxAC3ftIjgTEHsd/f+JrjW6t+kL6Ef1tT1eQ2DPFLJlhluTD91AsZMUg==||U2FsdGVkX1/SWWqU9YmxtM0T6Nm5mClKwqTaoF9wgZd9rNw2xs4hnY8Ilv8DZtFyNt92xym3eB6WA605N5llLm0D68EQtU9ci1rTEDopZ1ODzcqtTVSoFEloNPFSfW6LTIC9+2LSVBeeHXoLEQiLYHWihHu10Xll3KsH9iBObDACDm1PT7IV4uWvNpNeuKJc\npY3C5SG+3sHRX1aeMnHlKLhaIsOdw2IexjvMqocVpfRpX4wnsabNA0VJ3k95zUPS3vTtSegeDhwbl6j+/FZcGk9i+gAy6LuetlKuARjPYn2LH5Be3Ah+ggSBPlxf3JW9rtWNdUoFByHTcFlhzlU9HnpnBUrgcVMhCQ7SAjN9h2NMGmCr10Rn4OE0WtelNqYVig7KmENaPvFT+k2I0cYZ4KWwxxsQNKbjEAxJxrzK4HkaczCvyQbzj4Ppxx/0q+Cns44OeyWcwYD/vSaJm4Kptwpr+L4y5BoSO/WeqhSUQQ85nvOhtE0pSH/ZXYo3pqjPdQRfNm6NFeBl2lwTmZUEuw==\n====END LICENSE KEY====",
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
