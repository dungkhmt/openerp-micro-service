import { createBrowserRouter } from "react-router-dom";
import Layout from "../layout/Layout";
import DashBoard from "../pages/dashboard";
import { ProjectContextProvider } from "../pages/project/[id]/ProjectContextProvider";
import Project from "../pages/project/[id]/[tab]";
import Task from "../pages/project/[id]/task/[id]";
import { TaskContextProvider } from "../pages/project/[id]/task/[id]/TaskContextProvider";
import Projects from "../pages/projects";
import NewProject from "../pages/projects/new";
import TaskAssigned from "../pages/tasks/assign-me";
import NotFound from "../views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import Redirect from "./Redirect";

export const router = createBrowserRouter([
  {
    element: <Layout />,
    children: [
      {
        path: "/",
        element: <Redirect to="/dashboard" />,
      },
      {
        path: "/dashboard",
        element: <DashBoard />,
      },
      {
        element: <PrivateRoute />,
        children: [
          {
            path: "/projects",
            element: <Projects />,
            children: [
              {
                path: "new",
                element: <NewProject />,
              },
            ],
          },
          {
            path: "/project",
            element: <ProjectContextProvider />,
            children: [
              {
                path: ":id",
                element: <Redirect to="/project/:id/overview" />,
                children: [
                  {
                    element: <TaskContextProvider />,
                    children: [
                      {
                        path: "task/:id",
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
