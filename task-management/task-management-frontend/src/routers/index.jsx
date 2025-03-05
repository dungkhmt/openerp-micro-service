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
import { AttributeManager } from "../pages/task-attributes";

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
                    path: ":id/event/:eid",
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
            path: "/user-management",
            element: <UserManagement />,
          },
          {
            path: "/attribute-management",
            element: <AttributeManager />,
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
