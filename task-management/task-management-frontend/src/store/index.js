import { configureStore } from "@reduxjs/toolkit";
import project from "./project";
import tasks from "./project/tasks";
import category from "./category";
import status from "./status";
import priority from "./priority";

export const store = configureStore({
  reducer: {
    project,
    tasks,
    category,
    status,
    priority,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});
