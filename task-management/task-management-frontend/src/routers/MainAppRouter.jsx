import { LinearProgress } from "@mui/material";
import { Suspense, useEffect } from "react";
import {
  Navigate,
  Route,
  Routes,
  useLocation,
  useParams,
} from "react-router-dom";
import Layout from "../layout/Layout";
import { drawerWidth } from "../layout/sidebar/SideBar";
import DashBoard from "../pages/dashboard";
import { ProjectContextProvider } from "../pages/project/[id]/ProjectContextProvider";
import Project from "../pages/project/[id]/[tab]";
import Task from "../pages/project/[id]/task/[id]";
import { TaskContextProvider } from "../pages/project/[id]/task/[id]/TaskContextProvider";
import EditTask from "../pages/project/[id]/task/[id]/edit";
import Projects from "../pages/projects";
import NewProject from "../pages/projects/new";
import TaskAssigned from "../pages/tasks/assign-me";
import { useNotificationState } from "../state/NotificationState";
import NotFound from "../views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";

const styles = {
  loadingProgress: {
    position: "fixed",
    top: 0,
    left: -drawerWidth,
    width: "calc(100% + 300px)",
    zIndex: 1202,
    "& div": {
      top: "0.5px",
    },
  },
};

const RedirectToOverview = () => {
  const { id } = useParams();
  return <Navigate to={`/project/${id}/overview`} replace />;
};

function MainAppRouter() {
  const location = useLocation();
  const notificationState = useNotificationState();

  useEffect(() => {
    notificationState.open.set(false);
  }, [location.pathname, notificationState.open]);

  return (
    <Layout>
      <Suspense fallback={<LinearProgress sx={styles.loadingProgress} />}>
        <Routes>
          {/* Auto redirect to dashboard */}
          <Route path="/" element={<Navigate to="/dashboard" />} />
          <Route element={<DashBoard />} path="/dashboard" />
          <Route element={<PrivateRoute />}>
            {/* Projects */}
            <Route element={<Projects />} path={`/projects`} />
            <Route element={<NewProject />} path={`/projects/new`} />

            {/* Project */}
            <Route element={<ProjectContextProvider />}>
              <Route element={<Project />} path={`/project/:id/:tab`} />
              <Route
                path="/project/:id"
                element={
                  <RedirectToOverview />
                }
              />

              {/* Task */}
              <Route element={<TaskContextProvider />}>
                <Route element={<Task />} path={`/project/:id/task/:tid`} />
                <Route
                  element={<EditTask />}
                  path={`/project/:id/task/:tid/edit`}
                />
              </Route>
            </Route>

            {/* Tasks */}
            <Route element={<TaskAssigned />} path={`/tasks/assign-me`} />
          </Route>
          <Route element={<NotFound />} path="*" />
        </Routes>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
