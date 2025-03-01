import { Route, Switch, useRouteMatch } from "react-router-dom";
import CreateHub from "../screens/hub/createHub";
import ListHub from "../screens/hub/ListHub";
import UpdateHub from "../screens/hub/UpdateHub";

export default function HubManagerRouters() {
    let { path } = useRouteMatch();
    console.log("Base path:", path); // Add this line for debugging

    return (
        <div>
            <Switch>
                <Route
                    component={ListHub}
                    exact
                    path={`${path}/hublist`}
                />
                <Route
                    component={CreateHub}
                    exact
                    path={`${path}/createhub`}
                />
                <Route
                    component={UpdateHub}
                    exact
                    path={`${path}/hub/update/:id`}
                />
            </Switch>
        </div>
    );
}
