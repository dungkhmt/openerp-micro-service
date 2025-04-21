import { createBrowserRouter } from "react-router-dom";
import Layout from "../layout/Layout";
import Home from "../pages";
import DashBoard from "../pages/dashboard";
import MyProfile from "../pages/my-profile";
import UserManagement from "../pages/user-management";
import { ProjectWrapper } from "../pages/project/[id]/ProjectWrapper";
import Project from "../pages/project/[id]/[tab]";
import Task from "../pages/project/[id]/task/[id]";
import Event from "../pages/project/[id]/event/[id]";
import { TaskContextProvider } from "../pages/project/[id]/task/[id]/TaskContextProvider";
import Projects from "../pages/projects";
import NewProject from "../pages/projects/new";
import TaskAssigned from "../pages/tasks/assign-me";
import TaskCreated from "../pages/tasks/create-by-me";
import NotFound from "../views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import Redirect from "./Redirect";
import StaffScreen from "../views/hr/StaffScreen.jsx";
import DepartmentScreen from "../views/hr/DepartmentScreen.jsx";
import JobPositionScreen from "../views/hr/JobPositionScreen.jsx";
import CheckpointConfigureScreen from "../views/hr/CheckpointConfigureScreen.jsx";
import CheckpointPeriodScreen from "../views/hr/CheckpointPeriodScreen.jsx";
import { AttributeManager } from "../pages/task-attributes";
import CreatedMeetings from "../pages/meetings/created-meetings";
import CreatedMeeting from "../pages/meetings/created-meetings/[id]";
import JoinedMeetings from "../pages/meetings/joined-meetings";
import JoinedMeeting from "../pages/meetings/joined-meetings/[id]";
import Unknown from "../views/errors/Unknown";
import EmployeeDetails from "../views/hr/EmployeeDetailsScreen.jsx";
import CheckpointEvaluation from "../views/hr/CheckpointEvaluationScreen.jsx";
import CheckinoutScreen from "../views/hr/CheckinoutScreen.jsx";
import AttendancePage from "../views/hr/AttendancePage.jsx";
import HolidayScreen from "../views/hr/HolidayScreen.jsx";
import AnnounceAbsenceForm from "../views/hr/AnnounceAbsenceForm.jsx";


export const router = createBrowserRouter([
  {
    element: <Layout />,
    children: [
      {
        path: "/",
        element: <Home />,
      },
      {
        element: <PrivateRoute />,
        children: [
          {
            path: "/dashboard",
            element: <DashBoard />,
          },
          {
            path: "/my-profile",
            element: <MyProfile />,
          },
          {
            path: "/projects",
            children: [
              {
                path: "new",
                element: <NewProject />,
              },
              {
                path: "",
                element: <Projects />,
              },
            ],
          },
          {
            path: "/project",
            element: <ProjectWrapper />,
            children: [
              {
                children: [
                  {
                    path: ":id",
                    element: <Redirect to="/project/:id/overview" />,
                  },
                  {
                    element: <TaskContextProvider />,
                    children: [
                      {
                        path: ":id/task/:tid",
                        element: <Task />,
                      },
                    ],
                  },
                  {
                    path: ":id/events/:eid",
                    element: <Event />,
                  },
                ],
              },
              {
                path: ":id/:tab",
                element: <Project />,
              },
            ],
          },
          {
            path: "/tasks",
            children: [
              {
                path: "assign-me",
                element: <TaskAssigned />,
              },
              {
                path: "created-by-me",
                element: <TaskCreated />,
              },
            ],
          },
          {
            path: "/hr",
            children: [
              {
                path: "checkin-out",
                element: <CheckinoutScreen />
              },
              {
                path: "attendance-list",
                element: <AttendancePage />
              },
              {
                path: "staff",
                element: <StaffScreen />,
              },
              {
                path: "staff/:staffCode",
                element: <EmployeeDetails />,
              },
              {
                path: "department",
                element: <DepartmentScreen />,
              },
              {
                path: "job-position",
                element: <JobPositionScreen />,
              },
              {
                path: "checkpoint",
                children: [
                  {
                    path: "configure",
                    element: <CheckpointConfigureScreen/>,
                  },
                  {
                    path: "period",
                    element: <CheckpointPeriodScreen/>,
                  },
                  {
                    path: "evaluation",
                    element: <CheckpointEvaluation />,
                  },
                ]
              },
              {
                path: "absence",
                children: [
                  {
                    path: "announce",
                    element: <AnnounceAbsenceForm/>,
                  },
                  {
                    path: "me",
                    element: <AnnounceAbsenceForm/>,
                  },
                  {
                    path: "list",
                    element: <AnnounceAbsenceForm />,
                  },
                ]
              },
              {
                path: "holiday-schedule",
                element: <HolidayScreen />,
              },
            ],
          },
          {
            path: "/meetings",
            children: [
              {
                path: "created-meetings",
                element: <CreatedMeetings />,
              },
              {
                path: "created-meetings/:pid",
                element: <CreatedMeeting />,
              },
              {
                path: "joined-meetings",
                element: <JoinedMeetings />,
              },
              {
                path: "joined-meetings/:pid",
                element: <JoinedMeeting />,
              },
            ],
          },
          {
            path: "/user-management",
            element: <UserManagement />,
          },
          {
            path: "/attribute-management",
            element: <AttributeManager />,
          },
          {
            path: "/unknown-error",
            element: <Unknown />,
          },
          {
            path: "*",
            element: <NotFound />,
          },
        ],
      },
    ],
  },
]);
