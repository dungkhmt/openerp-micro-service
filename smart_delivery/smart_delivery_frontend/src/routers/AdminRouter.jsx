import { Route, Switch, useRouteMatch } from "react-router-dom";
import AlgorithmManagement from "../screens/admin/AlgorithmManagement";


export default function AdminRouter() {
    let { path } = useRouteMatch();
    console.log("Base path:", path); // Add this line for debugging

    return (
        <div>
            <Switch>
                <Route
                    component={AlgorithmManagement}
                    exact
                    path={`${path}/algorithm`}
                />
            </Switch>
        </div>
    );
}
