import { Route, Switch, useRouteMatch } from "react-router-dom";
import CreateEmployee from "../screens/human/createEmployee";
import CollectorList from "../screens/human/collector/CollectorList";
import ShipperList from "../screens/human/shipper/ShipperList";
import DriverList from "../screens/human/driver/DriverList";
import createEmployee from "../screens/human/createEmployee";
import UpdateCollector from "../screens/human/collector/UpdateCollector";
import UpdateShipper from "../screens/human/shipper/UpdateShipper";


export default function HumanManagerRouters() {
    let { path } = useRouteMatch();
    //console.log("Base path:", path); // Add this line for debugging

    return (
        <div>
            <Switch>
                <Route
                    component={CollectorList}
                    exact
                    path={`${path}/collector`}
                />
                <Route
                    component={UpdateCollector}
                    exact
                    path={`${path}/collector/update/:id`}
                />
                <Route
                    component={ShipperList}
                    exact
                    path={`${path}/shipper`}
                />
                <Route
                    component={UpdateShipper}
                    exact
                    path={`${path}/shipper/update/:id`}
                />
                <Route
                    component={DriverList}
                    exact
                    path={`${path}/driver`}
                />
                <Route
                    component={UpdateShipper}
                    exact
                    path={`${path}/driver/update/:id`}
                />
                <Route
                    component={createEmployee}
                    exact
                    path={`${path}/add`}
                />

            </Switch>
        </div>
    );
}
