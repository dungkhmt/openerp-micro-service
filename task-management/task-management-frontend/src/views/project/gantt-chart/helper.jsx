import dayjs from "dayjs";
import { ViewMode } from "gantt-task-react";

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

// ! This a hack to make the gantt chart work if there is no data
export const defaultTasks = Array.from({ length: 10 }, (_, index) => ({
  start: new Date(),
  end: new Date(),
  id: `row-${index}`,
  displayOrder: index + 1,
}));

export function convertTasks(tasks) {
  const closedStatuses = [
    "TASK_RESOLVED",
    "TASK_CLOSED",
    "ASSIGNMENT_INACTIVE",
  ];
  return tasks.length > 0
    ? tasks.map((task, index) => ({
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
      }))
    : defaultTasks;
}
