import {Route, Switch, useRouteMatch} from "react-router-dom";
import RoutesList from "../screens/middle-mile/RoutesList";
import RouteDetail from "../screens/middle-mile/RouteDetail";
import CreateRoute from "../screens/middle-mile/CreateRoute";
import VehicleAssignments from "../screens/middle-mile/VehicleAssignmentsDetail";
import ScheduleView from "../screens/middle-mile/ScheduleView";
import TripManagement from "../screens/middle-mile/TripManagement";
import OrderAssignment from "../screens/middle-mile/OrderAssignment";
import VehicleDetail from "../screens/middle-mile/VehicleDetail";
import VehicleAssignmentsDetail from "../screens/middle-mile/VehicleAssignmentsDetail";
import DriverVehicleManage from "../screens/middle-mile/DriverVehicleManage";
import DriverManagement from "../screens/middle-mile/DriverManagement";
import VehicleManagement from "../screens/middle-mile/VehicleManagement";
import DriverVehicleAssignmentManagement from "../screens/middle-mile/DriverVehicleAssignmentManagement";
import DriverDashboard from "../screens/middle-mile/DriverDashboard";
import DriverOrderManagement from "../screens/middle-mile/DriverOrderManagement";
import DriverRouteMap from "../screens/middle-mile/DriverRouteMap";

export default function MiddleMileRouter() {
    let {path} = useRouteMatch();

    return (
        <div>
            <Switch>
                <Route exact path={`${path}/routes`} component={RoutesList}/>
                <Route exact path={`${path}/routes/create`} component={CreateRoute}/>
                <Route exact path={`${path}/routes/edit/:routeId`} component={CreateRoute}/>
                <Route exact path={`${path}/routes/:routeId`} component={RouteDetail}/>
                <Route exact path={`${path}/vehicle-assignments/:assignmentId`} component={VehicleAssignmentsDetail}/>

                <Route
                    exact path={`${path}/vehicles/:id`} component={VehicleDetail}
                />
                <Route exact path={`${path}/schedule`} component={ScheduleView}/>
                <Route exact path={`${path}/trips`} component={TripManagement}/>
                <Route exact path={`${path}/trips/:routeVehicleId/orders`} component={OrderAssignment}/>
                <Route exact path={`${path}/driver/manage`} component={DriverManagement}/>
                <Route exact path={`${path}/vehicle/manage`} component={VehicleManagement}/>
                <Route exact path={`${path}/driver-vehicle/manage`} component={DriverVehicleAssignmentManagement}/>
                <Route exact path={`${path}/driver/dashboard`} component={DriverDashboard}/>
                <Route exact path={`${path}/driver/orders/:routeVehicleId`} component={DriverOrderManagement}/>
                <Route exact path={`${path}/driver/route/:routeVehicleId`} component={DriverRouteMap}/>

            </Switch>
        </div>
    );
}