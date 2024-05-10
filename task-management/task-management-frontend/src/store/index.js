import { configureStore } from "@reduxjs/toolkit";
import project from "./project";
import tasks from "./project/tasks";
import category from "./category";
import status from "./status";
import priority from "./priority";
import assignedTasks from "./assigned-tasks";
import gantt from "./project/gantt-chart";
import calendar from "./project/calendar";
import createdTasks from "./created-tasks";

export const store = configureStore({
  reducer: {
    project,
    tasks,
    category,
    status,
    priority,
    assignedTasks,
    gantt,
    calendar,
    createdTasks,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});
