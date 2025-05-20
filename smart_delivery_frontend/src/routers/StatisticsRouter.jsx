import { Route, Switch, useRouteMatch } from "react-router-dom";
import CreateHub from "../screens/hub/createHub";
import ListHub from "../screens/hub/ListHub";
import UpdateHub from "../screens/hub/UpdateHub";
import EmployeeOrderHistory from "../screens/analytics/EmployeeOrderHistory";
import DriverTripHistory from "../screens/analytics/DriverTripHistory";

export default function StatisticsRouter() {
    let { path } = useRouteMatch();
    console.log("Base path:", path); // Add this line for debugging

    return (
        <div>
            <Switch>
                <Route
                component={EmployeeOrderHistory}
                exact
                path={`${path}/me`}
            />
                <Route
                    component={DriverTripHistory}
                    exact
                    path={`${path}/driver`}
                />
            </Switch>
        </div>
    );
}
