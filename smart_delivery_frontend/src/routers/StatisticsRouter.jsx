import { Route, Switch, useRouteMatch } from "react-router-dom";
import CreateHub from "../screens/hub/createHub";
import ListHub from "../screens/hub/ListHub";
import UpdateHub from "../screens/hub/UpdateHub";
import EmployeeStatistics from "../screens/analytics/EmployeeStatistics";

export default function StatisticsRouter() {
    let { path } = useRouteMatch();
    console.log("Base path:", path); // Add this line for debugging

    return (
        <div>
            <Switch>
                <Route
                    component={EmployeeStatistics}
                    exact
                    path={`${path}/me`}
                />
            </Switch>
        </div>
    );
}
