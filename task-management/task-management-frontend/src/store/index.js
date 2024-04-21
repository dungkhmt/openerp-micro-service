import { configureStore } from "@reduxjs/toolkit";
import project from "./project";
import tasks from "./project/tasks";
import category from "./category";
import status from "./status";
import priority from "./priority";
import assignedTasks from "./assigned-tasks";

export const store = configureStore({
  reducer: {
    project,
    tasks,
    category,
    status,
    priority,
    assignedTasks,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});
