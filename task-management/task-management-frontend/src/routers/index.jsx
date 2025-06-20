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
import CreatedMeetings from "../pages/meetings/created-meetings";
import CreatedMeetingDetails from "../pages/meetings/created-meetings/[id]";
import JoinedMeetings from "../pages/meetings/joined-meetings";
import JoinedMeetingDetails from "../pages/meetings/joined-meetings/[id]";
import Unknown from "../views/errors/Unknown";
import UserGroups from "../pages/user-management/group";
import GroupDetails from "../pages/user-management/group/[id]";
import NewOrganization from "../pages/organization/new";
import OrganizationMembersView from "../pages/organization/settings/OrganizationMembersView";
import MyInvitationsPage from "../views/organization/MyInvitationsTable";
import InvitationDetail from "../views/organization/InvitationDetail";

export const router = createBrowserRouter([
  {
    element: <Layout />,
    children: [
      {
        path: "/",
        element: <Home />,
      },
      {
        path: "/new-organization",
        element: <NewOrganization />,
      },
      {
        path: "/invitations",
        children: [
          {
            path: "",
            element: <MyInvitationsPage />,
          },
          {
            path: ":invitationToken",
            element: <InvitationDetail />,
          },
        ],
      },
      {
        element: <PrivateRoute />,
        children: [
          {
            path: "/dashboard",
            element: <DashBoard />,
          },
          {
            path: "/settings/members",
            element: <OrganizationMembersView />,
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
            path: "/meetings",
            children: [
              {
                path: "created-meetings",
                element: <CreatedMeetings />,
              },
              {
                path: "created-meetings/:meetingId",
                element: <CreatedMeetingDetails />,
              },
              {
                path: "joined-meetings",
                element: <JoinedMeetings />,
              },
              {
                path: "joined-meetings/:meetingId",
                element: <JoinedMeetingDetails />,
              },
            ],
          },
          {
            path: "/user-management",
            children: [
              {
                path: "groups",
                element: <UserGroups />,
              },
              {
                path: "groups/:groupId",
                element: <GroupDetails />,
              },
              {
                path: "",
                element: <UserManagement />,
              },
            ],
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
