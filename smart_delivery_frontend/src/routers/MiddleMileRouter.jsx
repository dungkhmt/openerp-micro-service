import {Route, Switch, useRouteMatch} from "react-router-dom";
import RoutesList from "../screens/middle-mile/RoutesList";
import RouteDetail from "../screens/middle-mile/RouteDetail";
import CreateRoute from "../screens/middle-mile/CreateRoute";
import VehicleAssignments from "../screens/middle-mile/VehicleAssignments";
import ScheduleView from "../screens/middle-mile/ScheduleView";
import TripManagement from "../screens/middle-mile/TripManagement";
import OrderAssignment from "../screens/middle-mile/OrderAssignment";
import VehicleDetail from "../screens/middle-mile/VehicleDetail";

export default function MiddleMileRouter() {
    let {path} = useRouteMatch();

    return (
        <div>
            <Switch>
                <Route exact path={`${path}/routes`} component={RoutesList}/>
                <Route exact path={`${path}/routes/create`} component={CreateRoute}/>
                <Route exact path={`${path}/routes/edit/:routeId`} component={CreateRoute}/>
                <Route exact path={`${path}/routes/:routeId`} component={RouteDetail}/>
                <Route exact path={`${path}/vehicle-assignments`} component={VehicleAssignments}/>
                <Route
                    exact path={`${path}/vehicles/:id`} component={VehicleDetail}
                />
                <Route exact path={`${path}/schedule`} component={ScheduleView}/>
                <Route exact path={`${path}/trips`} component={TripManagement}/>
                <Route exact path={`${path}/trips/:routeVehicleId/orders`} component={OrderAssignment}/>
            </Switch>
        </div>
    );
}