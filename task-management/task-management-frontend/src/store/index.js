import { configureStore } from "@reduxjs/toolkit";
import assignedTasks from "./assigned-tasks";
import category from "./category";
import createdMeetings from "./created-meetings";
import createdTasks from "./created-tasks";
import joinedMeetings from "./joined-meetings";
import meetingPlan from "./meeting-plan";
import meetingSessions from "./meeting-plan/meeting-sessions";
import myProfile from "./my-profile";
import organization from "./organization";
import invitation from "./organization/invitation";
import priority from "./priority";
import project from "./project";
import calendar from "./project/calendar";
import events from "./project/events";
import gantt from "./project/gantt-chart";
import statistic from "./project/statistic";
import tasks from "./project/tasks";
import skill from "./skill";
import status from "./status";
import userManagement from "./user-management";
import userGroup from "./user-management/group";
import search from "./search"

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
    userGroup,
    organization,
    invitation,
    events,
    meetingPlan,
    meetingSessions,
    createdMeetings,
    joinedMeetings,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});
