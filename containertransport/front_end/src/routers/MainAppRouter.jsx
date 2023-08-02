import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import NotFound from "views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import TeacherRouter from "./TeacherRouter";
import TruckScreen from "views/truck/TruckScreen";
import FacilityScreen from "views/facilityManagement/FacilityScreen";
import OrderScreen from "views/order/OrderScreen";
import ContainerScreen from "views/containerManagerment/ContainerScreen";
import ShipmentScreen from "views/shipment/ShipmentScreen";
import { AppProvider } from "contextAPI/MyContext";
import CreateTripDetail from "views/shipment/tripCreate/CreateTripDetail";
import ShipmentDetail from "views/shipment/shipmentDetail/ShipmentDetail";
import TripDetail from "views/shipment/tripDetail/TripDetail";
import TrailerScreen from "views/trailer/TrailerScreen";
import FacilityDetail from "views/facilityManagement/detail/FacilityDetail";
import TruckDetail from "views/truck/detail/TruckDetail";
import OrderDetail from "views/order/detail/OrderDetail";
import TripPendingScreen from "views/trip/tripPending/TripPendingScreen";
import TripManaDetail from "views/trip/component/TripManaDetail";
import DetailContainerScreen from "views/containerManagerment/detail/DetailContainersScreen";
import DetailTrailer from "views/trailer/detail/DetailTrailer";
import OrderWaitApprove from "views/order/waitApprove/OrderWaitApprove";
import TypeContainer from "views/containerManagerment/typeContainer/TypeContainer";
import Dashboard from "views/dashboard/Dashboard";
import TripExecutedScreen from "views/trip/tripExecuted/TripExecutedScreen";

const styles = {
  loadingProgress: {
    position: "fixed",
    top: 0,
    left: -drawerWidth,
    width: "calc(100% + 300px)",
    zIndex: 1202,
    "& div": {
      top: "0.5px",
    },
  },
};

function MainAppRouter(props) {
  const location = useLocation();
  const notificationState = useNotificationState();

  useEffect(() => {
    notificationState.open.set(false);
  }, [location.pathname]);

  return (
    <Layout>
      <Suspense fallback={<LinearProgress sx={styles.loadingProgress} />}>
        <Switch>

          <Route component={() => <></>} exact path="/" />
          <AppProvider>
            <PrivateRoute component={Dashboard} path="/dashboard" />
            <PrivateRoute component={TeacherRouter} path="/teacher" />
            <PrivateRoute component={TruckScreen} exact path="/truck" />
            <PrivateRoute component={TruckDetail} exact path="/truck/detail/:truckId" />

            <PrivateRoute component={FacilityScreen} exact path="/facility" />
            <PrivateRoute component={FacilityDetail} exact path="/facility/detail/:facilityId" />

            <PrivateRoute component={OrderScreen} exact path="/order/" />
            <PrivateRoute component={OrderWaitApprove} exact path="/wait-approve/order" />
            <PrivateRoute component={OrderDetail} exact path="/order/:uid" />
            <PrivateRoute component={OrderDetail} exact path="/wait-approve/order/:type/:uid" />

            <PrivateRoute component={ContainerScreen} exact path="/container" />
            <PrivateRoute component={DetailContainerScreen} exact path="/container/detail/:containerId" />
            <PrivateRoute component={TypeContainer} exact path="/type/container" />

            <PrivateRoute component={TrailerScreen} exact path="/trailer" />
            <PrivateRoute component={DetailTrailer} exact path="/trailer/detail/:trailerId" />

            <PrivateRoute component={TripPendingScreen} exact path="/trip/pending" />
            <PrivateRoute component={TripManaDetail} exact path="/trip/detail/:type/:tripId" />
            <PrivateRoute component={TripExecutedScreen} exact path="/trip/executed" />

            <PrivateRoute component={ShipmentScreen} exact path="/shipment" />
            <PrivateRoute component={ShipmentDetail} exact path="/shipment/detail/:shipmentId" />
            <PrivateRoute component={TripDetail} exact path="/shipment/trip/detail/:shipmentId/:tripId" />
            <PrivateRoute component={CreateTripDetail} path="/shipment/trip/create/:shipmentId" />
          </AppProvider>
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
