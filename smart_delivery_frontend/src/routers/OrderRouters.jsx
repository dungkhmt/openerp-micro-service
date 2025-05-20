import { Route, Switch, useRouteMatch } from "react-router-dom";
import CreateHub from "../screens/hub/createHub";
import ListHub from "../screens/hub/ListHub";
import UpdateHub from "../screens/hub/UpdateHub";
import createOrder from "../screens/order/createOrder";
import OrderList from "../screens/order/OrderList";
import UpdateOrder from "../screens/order/UpdateOrder";
import AssignOrder from "../screens/order/AssignOrder";
import TodayOrder from "../screens/order/TodayOrder";
import CollectorOrderDetail from "../screens/order/CollectorOrderDetail";
import InOrder from "../screens/inOut/InOrder";
import OutOrder from "../screens/inOut/OutOrder";
import TripOrderItemsOut from "../screens/inOut/TripOrderItemsOut";
import TripOrderItemsIn from "../screens/inOut/TripOrderItemsIn";
import AssignOrderShipper from "../screens/order/AssignOrderShipper";
export default function OrderRouters() {
    let { path } = useRouteMatch();
    console.log("Base path:", path); // Add this line for debugging

    return (
        <div>
            <Switch>
                <Route
                    component={OrderList}
                    exact
                    path={`${path}/orderlist`}
                />
                <Route
                    component={createOrder}
                    exact
                    path={`${path}/createorder`}
                />
                <Route
                    component={UpdateOrder}
                    exact
                    path={`${path}/update/:id`}
                />
                <Route
                    component={UpdateOrder}
                    exact
                    path={`${path}/view/:id`}
                />
                <Route
                    component={AssignOrder}
                    exact
                    path={`${path}/assign/collector`}
                />
                <Route
                    component={AssignOrderShipper}
                    exact
                    path={`${path}/assign/shipper`}
                />
                <Route
                    component={TodayOrder}
                    exact
                    path={`${path}/assign/today/me`}
                />
                <Route
                    component={CollectorOrderDetail}
                    exact
                    path={`${path}/collector/:id`}
                />
                <Route
                    component={CollectorOrderDetail}
                    exact
                    path={`${path}/shipper/:id`}
                />
                <Route
                    component={InOrder}
                    exact
                    path={`${path}/confirm/in`}
                />
                <Route
                    component={OutOrder}
                    exact
                    path={`${path}/out/confirm`}
                />
                <Route
                    component={TripOrderItemsOut}
                    exact
                    path={`${path}/trip/items/:tripId/out`}
                />
                <Route
                    component={TripOrderItemsIn}
                    exact
                    path={`${path}/trip/items/:tripId/in`}
                />

            </Switch>
        </div>
    );
}
