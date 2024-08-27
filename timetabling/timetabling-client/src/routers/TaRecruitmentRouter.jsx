import { Route, Switch, useRouteMatch } from "react-router";
import AllClassScreen from "views/tarecruitment/AllClassScreen/index";
import AllRegisterClassScreen from "views/tarecruitment/AllRegisterClassScreen";
import ApplicationResultScreen from "views/tarecruitment/ApplicationResultScreen";
import AssigningScreen from "views/tarecruitment/AssigningScreen";
import ClassInformationScreen from "views/tarecruitment/ClassInformationScreen";
import Dashboard from "views/tarecruitment/Dashboard";
import RegisterClassForStudentScreen from "views/tarecruitment/RegisterClassForStudentScreen";
import RegisterClassScreen from "views/tarecruitment/RegisterClassScreen";
import RequestApprovalScreen from "views/tarecruitment/RequestApprovalScreen";
import TAAssistListScreen from "views/tarecruitment/TAAssistListScreen";
import UpdateApplicationPage from "views/tarecruitment/ApplicationResultScreen/UpdateApplicationPage";
import ApplicationDetailPage from "views/tarecruitment/ApplicationResultScreen/DetailApplicationPage";

export default function StudentRouter() {
  let { path } = useRouteMatch();
  console.log("Current path:", path);

  return (
    <div>
      <Switch>
        <Route
          component={RegisterClassForStudentScreen}
          exact
          path={`${path}/student/class-register`}
        ></Route>
        <Route
          component={AllRegisterClassScreen}
          exact
          path={`${path}/student/classregister-list`}
        ></Route>
        <Route
          component={ApplicationResultScreen}
          exact
          path={`${path}/student/result`}
        ></Route>

        <Route
          component={RegisterClassScreen}
          exact
          path={`${path}/teacher/create-class/:semester`}
        ></Route>
        <Route
          component={Dashboard}
          exact
          path={`${path}/teacher/dashboard`}
        ></Route>
        <Route
          component={AllClassScreen}
          exact
          path={`${path}/teacher/class-list`}
        ></Route>
        <Route
          component={ClassInformationScreen}
          exact
          path={`${path}/teacher/class-information/:id`}
        ></Route>
        <Route
          component={RequestApprovalScreen}
          exact
          path={`${path}/teacher/request-approval`}
        ></Route>
        <Route
          component={AssigningScreen}
          exact
          path={`${path}/teacher/ta-assignment`}
        ></Route>
        <Route
          component={TAAssistListScreen}
          exact
          path={`${path}/teacher/ta-assist-list`}
        ></Route>
        <Route
          component={UpdateApplicationPage}
          exact
          path={`${path}/student/result/:applicationId/edit`}
        />
        <Route
          component={ApplicationDetailPage}
          exact
          path={`${path}/student/result/:applicationId`}
        />
      </Switch>
    </div>
  );
}
