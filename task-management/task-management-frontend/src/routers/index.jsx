import { createBrowserRouter } from "react-router-dom";
import Layout from "../layout/Layout";
import Home from "../pages";
import DashBoard from "../pages/dashboard";
import { ProjectWrapper } from "../pages/project/[id]/ProjectWrapper";
import Project from "../pages/project/[id]/[tab]";
import Task from "../pages/project/[id]/task/[id]";
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
                path: "staff",
                element: <StaffScreen />,
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
                path: "job-position",
                element: <JobPositionScreen />,
              },
              {
                path: "checkpoint",
                children: [
                  {
                    path: "configure",
                    element: <CheckpointConfigureScreen />,
                  },
                  {
                    path: "period",
                    element: <CheckpointPeriodScreen />,
                  },
                ]
              },
            ],
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
