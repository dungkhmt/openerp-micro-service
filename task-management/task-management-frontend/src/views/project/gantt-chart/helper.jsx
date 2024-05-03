import { useTheme } from "@mui/material";
import dayjs from "dayjs";
import { ViewMode } from "gantt-task-react";
import { useEffect, useState } from "react";
import { hexToRGBA } from "../../../components/utils/hex-to-rgba";
import { getStatusColor } from "../../../utils/color.util";

export const viewModeOptions = [
  {
    id: ViewMode.Hour,
    label: "Giờ",
  },
  {
    id: ViewMode.HalfDay,
    label: "Nửa ngày",
  },
  {
    id: ViewMode.Day,
    label: "Ngày",
  },
  {
    id: ViewMode.Week,
    label: "Tuần",
  },
  {
    id: ViewMode.Month,
    label: "Tháng",
  },
  {
    id: ViewMode.Year,
    label: "Năm",
  },
];

export function useConvertTasks(tasks, start) {
  // ! This a hack to make the gantt chart work if there is no data
  const defaultTasks = Array.from({ length: 10 }, (_, index) => ({
    start: new Date(start),
    end: new Date(start),
    id: `row-${index}`,
    displayOrder: index + 1,
  }));

  const closedStatuses = [
    "TASK_RESOLVED",
    "TASK_CLOSED",
    "ASSIGNMENT_INACTIVE",
  ];
  const [ganttTasks, setGanttTasks] = useState(defaultTasks);
  const theme = useTheme();

  useEffect(() => {
    const newGanttTasks =
      tasks.length > 0
        ? tasks.map((task, index) => {
            const statusColor = getStatusColor(task.statusId);
            const hexColor = theme.palette[statusColor].main;
            return {
              ...task,
              start: dayjs(task.fromDate ?? task.createdStamp).toDate(),
              end: dayjs(task.dueDate ?? task.fromDate ?? task.createdStamp)
                .add(8, "hours")
                .toDate(),
              progress: task.progress || 0,
              type: "task",
              // TODO: check if the status is closed state
              isDisabled: closedStatuses.includes(task.statusId),
              displayOrder: index + 1,
              styles: {
                backgroundColor: hexToRGBA(hexColor, 0.28),
                backgroundSelectedColor: hexToRGBA(hexColor, 0.28),
                progressColor: hexToRGBA(hexColor, 0.9),
                progressSelectedColor: hexColor,
              },
              dependencies: [task.parentId],
            };
          })
        : defaultTasks;
    if (newGanttTasks.length < 10) {
      const append = Array.from(
        { length: 10 - newGanttTasks.length },
        (_, index) => ({
          start: new Date(start),
          end: new Date(start),
          id: `row-${10 - index}`,
          displayOrder: 10 - index,
        })
      ).reverse();
      setGanttTasks(newGanttTasks.concat(append));
    } else setGanttTasks(newGanttTasks);
  }, [tasks]);

  return [ganttTasks, setGanttTasks];
}
