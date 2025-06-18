import toast from "react-hot-toast";
import { resetTasksData as resetAssignedTasks } from "../assigned-tasks";
import { fetchCategories } from "../category";
import { resetCreatedMeetings } from "../created-meetings";
import { resetTasksData as resetCreatedTasks } from "../created-tasks";
import { resetJoinedMeetings } from "../joined-meetings";
import { resetMeetingPlans } from "../meeting-plan";
import { resetMeetingSessions } from "../meeting-plan/meeting-sessions";
import { fetchMyProfile } from "../my-profile";
import { resetInvitation } from "../organization/invitation";
import { fetchPriorities } from "../priority";
import { resetProject } from "../project";
import { resetCalendarData } from "../project/calendar";
import { resetEvents } from "../project/events";
import { resetGanttData } from "../project/gantt-chart";
import { resetStatistic } from "../project/statistic";
import { resetTasksData } from "../project/tasks";
import { resetRecent } from "../search";
import { fetchSkills } from "../skill";
import { fetchStatuses } from "../status";
import { fetchAllUsers, resetUserManagement } from "../user-management";
import { resetUserGroup } from "../user-management/group";

export const resetOrganizationData = (dispatch) => {
  dispatch(resetAssignedTasks());
  dispatch(resetCreatedTasks());
  dispatch(resetCreatedMeetings());
  dispatch(resetJoinedMeetings());
  dispatch(resetMeetingPlans());
  dispatch(resetMeetingSessions());
  dispatch(resetInvitation());
  dispatch(resetProject());
  dispatch(resetTasksData());
  dispatch(resetStatistic());
  dispatch(resetGanttData());
  dispatch(resetEvents());
  dispatch(resetCalendarData());
  dispatch(resetRecent());
  dispatch(resetUserGroup());
  dispatch(resetUserManagement());
};

export const fetchOrganizationData = async (dispatch, organizationId) => {
  try {
    await Promise.all([
      dispatch(fetchStatuses()),
      dispatch(fetchCategories()),
      dispatch(fetchPriorities()),
      dispatch(fetchSkills()),
      dispatch(fetchMyProfile()),
      dispatch(fetchAllUsers(organizationId)),
    ]);
  } catch (error) {
    console.error("Error fetching tenant data:", error);
    toast.error("Error fetching tenant data");
  }
};
