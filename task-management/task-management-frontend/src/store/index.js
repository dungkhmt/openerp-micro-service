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
import search from "./search";
import statistic from "./project/statistic";
import skill from "./skill";
import myProfile from "./my-profile";
import userManagement from "./user-management"
import events from "./project/events";

export const store = configureStore({
  reducer: {
    project,
    tasks,
    myProfile,
    category,
    status,
    priority,
    assignedTasks,
    gantt,
    calendar,
    createdTasks,
    search,
    statistic,
    skill,
    userManagement,
    events,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});
