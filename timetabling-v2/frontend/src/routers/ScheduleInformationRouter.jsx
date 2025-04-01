import ClassRoomScreen from "views/courseTimeTabling/classroom/ClassroomListScreen";
import ClassPeriodScreen from "views/scheduleInformation/ClassPeriodScreen";
import GroupScreen from "views/courseTimeTabling/group/ClassGroupList";
import SemesterScreen from "views/courseTimeTabling/semester/SemesterScreen";
import WeekDayScreen from "views/scheduleInformation/WeekDayScreen";
import CourseScreen from "views/courseTimeTabling/course/CourseScreen";
import { Route, Switch, useRouteMatch } from "react-router";
import WeekAcademicScreen from "views/courseTimeTabling/week-academic/WeekAcademicScreen";

export default function TeacherRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={SemesterScreen}
          exact
          path={`${path}/semester`}
        ></Route>
        <Route
          component={CourseScreen}
          exact 
          path={`${path}/course`}
        ></Route>
        <Route
          component={ClassRoomScreen}
          exact
          path={`${path}/classroom`}
        ></Route>
        <Route
          component={GroupScreen}
          exact
          path={`${path}/group`}
        ></Route>
        <Route
          component={ClassPeriodScreen}
          exact
          path={`${path}/class-period`}
        ></Route>
        <Route
          component={WeekDayScreen}
          exact
          path={`${path}/week-day`}
        ></Route>
        <Route
          component={WeekAcademicScreen}
          exact
          path={`${path}/academic-week`}
        ></Route>
      </Switch>
    </div>
  );
}
