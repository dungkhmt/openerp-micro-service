import { Box } from "@mui/material";
import GSTC from "gantt-schedule-timeline-calendar/dist/gstc.wasm.esm.min.js";
import "gantt-schedule-timeline-calendar/dist/style.css";
import { useCallback, useEffect, useState } from "react";
import { buildConfig } from "./config";
import {
  GSTCID,
  addDays,
  endDate,
  iterations,
  startDate,
  startTime,
} from "./config/lib";
import { colors, getRandomColor } from "./config/color";
import { usePreventOverflow } from "../../../hooks/usePreventOverflow";

function getRandomFaceImage() {
  return "https://cdn.vectorstock.com/i/1000v/31/95/user-sign-icon-person-symbol-human-avatar-vector-12693195.jpg";
}

function getInitialRows() {
  /**
   * @type {import("gantt-schedule-timeline-calendar/dist/gstc").Rows}
   */
  const rows = {};
  for (let i = 0; i < iterations; i++) {
    const withParent = i > 0 && i % 2 === 0;
    const id = GSTCID(String(i));
    rows[id] = {
      id,
      label: `John Doe ${i}`,
      parentId: withParent ? GSTCID(String(i - 1)) : undefined,
      expanded: false,
      vacations: [],
      img: getRandomFaceImage(),
      progress: Math.floor(Math.random() * 100),
      visible: true,
    };
  }

  rows[GSTCID("11")].label = "NESTED TREE HERE";
  rows[GSTCID("12")].parentId = GSTCID("11");
  rows[GSTCID("13")].parentId = GSTCID("12");
  rows[GSTCID("14")].parentId = GSTCID("13");
  rows[GSTCID("3")].vacations = [
    {
      from: startDate.add(5, "days").startOf("day").valueOf(),
      to: startDate.add(5, "days").endOf("day").valueOf(),
    },
    {
      from: startDate.add(6, "days").startOf("day").valueOf(),
      to: startDate.add(6, "days").endOf("day").valueOf(),
    },
  ];
  rows[GSTCID("7")].birthday = [
    {
      from: startDate.add(3, "day").startOf("day").valueOf(),
      to: startDate.add(3, "day").endOf("day").valueOf(),
    },
  ];
  return rows;
}

function generateItemsForDaysView() {
  /**
   * @type {import("gantt-schedule-timeline-calendar/dist/gstc").Items}
   */
  const items = {};
  for (let i = 0; i < iterations; i++) {
    let rowId = GSTCID(i.toString());
    let id = GSTCID(i.toString());
    let startDayjs = GSTC.api
      .date(startTime)
      .startOf("day")
      .add(Math.floor(Math.random() * addDays), "day");
    let end = startDayjs
      .clone()
      .add(Math.floor(Math.random() * 20) + 4, "day")
      .endOf("day")
      .valueOf();
    if (end > endDate.valueOf()) end = endDate.valueOf();
    items[id] = {
      id,
      label: `John Doe ${i}`,
      progress: Math.round(Math.random() * 100),
      style: { background: getRandomColor() },
      time: {
        start: startDayjs.startOf("day").valueOf(),
        end,
      },
      rowId,
      img: getRandomFaceImage(),
      classNames: ["additional-custom-class"],
      description: "Lorem ipsum dolor sit amet",
    };
  }

  items[GSTCID("0")].linkedWith = [GSTCID("1")];
  items[GSTCID("0")].label = "Task 0 linked with 1";
  items[GSTCID("0")].type = "task";
  items[GSTCID("1")].label = "Task 1 linked with 0";
  items[GSTCID("1")].type = "task";
  items[GSTCID("1")].time = { ...items[GSTCID("0")].time };

  items[GSTCID("0")].style = { background: colors[3] };
  items[GSTCID("1")].style = { background: colors[3] };

  items[GSTCID("3")].dependant = [GSTCID("5")];
  items[GSTCID("3")].label = "Grab and move me into vacation area";
  items[GSTCID("3")].time.start = GSTC.api
    .date(startTime)
    .add(4, "day")
    .startOf("day")
    .add(5, "day")
    .valueOf();
  items[GSTCID("3")].time.end = GSTC.api
    .date(items[GSTCID("3")].time.start)
    .endOf("day")
    .add(5, "day")
    .valueOf();

  items[GSTCID("5")].time.start = GSTC.api
    .date(items[GSTCID("3")].time.end)
    .startOf("day")
    .add(5, "day")
    .valueOf();
  items[GSTCID("5")].time.end = GSTC.api
    .date(items[GSTCID("5")].time.start)
    .endOf("day")
    .add(2, "day")
    .valueOf();
  items[GSTCID("5")].dependant = [GSTCID("7"), GSTCID("9")];

  items[GSTCID("7")].time.start = GSTC.api
    .date(items[GSTCID("5")].time.end)
    .startOf("day")
    .add(3, "day")
    .valueOf();
  items[GSTCID("7")].time.end = GSTC.api
    .date(items[GSTCID("7")].time.start)
    .endOf("day")
    .add(2, "day")
    .valueOf();
  items[GSTCID("9")].time.start = GSTC.api
    .date(items[GSTCID("5")].time.end)
    .startOf("day")
    .add(2, "day")
    .valueOf();
  items[GSTCID("9")].time.end = GSTC.api
    .date(items[GSTCID("9")].time.start)
    .endOf("day")
    .add(3, "day")
    .valueOf();
  return items;
}

const ProjectViewGanttChart = () => {
  const { ref, updateHeight } = usePreventOverflow();
  const [gstc, setGstc] = useState(null);

  const callback = useCallback((element) => {
    if (element) {
      const config = buildConfig({
        listRows: getInitialRows(),
        chartItems: generateItemsForDaysView(),
      });

      const state = GSTC.api.stateFromConfig(config);

      const gstc_ = GSTC({
        element,
        state,
      });

      setGstc(gstc_);
    }
  }, []);

  useEffect(() => {
    return () => {
      if (gstc) {
        gstc.destroy();
      }
    };
  });

  useEffect(() => {
    updateHeight(10);
  }, []);

  return (
    <Box ref={ref} sx={{ "& .gstc-wrapper": { height: "100%" } }}>
      <div className="gstc-wrapper" ref={callback}></div>
    </Box>
  );
};

export { ProjectViewGanttChart };
