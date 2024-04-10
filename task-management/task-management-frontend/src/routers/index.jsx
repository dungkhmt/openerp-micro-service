import { createBrowserRouter } from "react-router-dom";
import Layout from "../layout/Layout";
import DashBoard from "../pages/dashboard";
import { ProjectWrapper } from "../pages/project/[id]/ProjectWrapper";
import Project from "../pages/project/[id]/[tab]";
import Task from "../pages/project/[id]/task/[id]";
import { TaskContextProvider } from "../pages/project/[id]/task/[id]/TaskContextProvider";
import Projects from "../pages/projects";
import NewProject from "../pages/projects/new";
import TaskAssigned from "../pages/tasks/assign-me";
import NotFound from "../views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import Redirect from "./Redirect";
import Home from "../pages";

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
