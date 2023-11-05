import { LinearProgress } from "@mui/material";
import { Suspense, useEffect } from "react";
import { Route, Routes, useLocation } from "react-router-dom";
import ListAssignedTasks from "../components/assignedtasks/ListAssignedTasks";
import Board from "../components/board/Board";
import CommonManager from "../components/projects/CommonManager";
import ListProject from "../components/projects/ListProject";
import CreateTask from "../components/task/CreateTasks";
import EditTask from "../components/task/EditTask";
import ListTasks from "../components/task/ListTasks";
import ShowTask from "../components/task/ShowTask";
import Layout from "../layout/Layout";
import { drawerWidth } from "../layout/sidebar/SideBar";
import { useNotificationState } from "../state/NotificationState";
import NotFound from "../views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import CreateProject from "../components/projects/CreateProject";

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
          <Route element={<h1>Welcome to</h1>} path="/" />
          <Route element={<PrivateRoute />}>
            <Route
              element={<CreateProject />}
              path={`/project/type/:type/:projectId?`}
            ></Route>
            <Route element={<ListProject />} path={`/project/list`}></Route>
            <Route
              element={<CreateTask />}
              path={`/project/tasks/create/:projectIdUrl?`}
            ></Route>
            <Route element={<CommonManager />} path={`/common-manager`}></Route>
            <Route
              element={<ListTasks />}
              path={`/project/:projectId/tasks`}
            ></Route>
            <Route
              element={<ListAssignedTasks />}
              path={`/tasks/members/assigned`}
            ></Route>
            <Route element={<ShowTask />} path={`/tasks/:taskId`}></Route>
            <Route element={<EditTask />} path={`/tasks/:taskId/edit`}></Route>
            <Route
              element={<Board />}
              path={`/project/:projectId/board`}
            ></Route>
          </Route>
          <Route element={<NotFound />} path="*" />
        </Routes>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
