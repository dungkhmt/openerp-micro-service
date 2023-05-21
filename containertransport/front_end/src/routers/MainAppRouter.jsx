import { LinearProgress } from "@mui/material";
import { Layout } from "layout";
import { drawerWidth } from "layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import { useNotificationState } from "state/NotificationState";
import NotFound from "views/errors/NotFound";
import PrivateRoute from "./PrivateRoute";
import TeacherRouter from "./TeacherRouter";
import TruckScreen from "views/truckManagement/TruckScreen";
import FacilityScreen from "views/facilityManagement/FacilityScreen";
import OrderScreen from "views/order/OrderScreen";
import ContainerScreen from "views/containerManagerment/ContainerScreen";
import ShipmentScreen from "views/shipment/ShipmentScreen";
import TripScreen from "views/shipment/shipmentCreate.js/CreateTripScreen";
import { AppProvider } from "contextAPI/MyContext";
import TripsScreen from "views/shipment/shipmentDetail/TripsScreen";
import CreateTripDetail from "views/shipment/shipmentCreate.js/CreateTripDetail";
import TripDetail from "views/shipment/shipmentDetail/TripDetail";

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
          <PrivateRoute component={TeacherRouter} path="/teacher" />
          <PrivateRoute component={TruckScreen} path="/truck" />
          <PrivateRoute component={FacilityScreen} path="/facility" />
          <PrivateRoute component={OrderScreen} path="/order" />
          <PrivateRoute component={ContainerScreen} path="/container" />
          {/* <Route component={ShipmentScreen} path="/trailer" /> */}
          <AppProvider>
            <Route component={ShipmentScreen} exact path="/shipment" />
            <Route component={TripsScreen} exact path="/shipment/detail/:shipmentId" />
            <Route component={TripDetail} exact path="/shipment/trip/detail/:tripId" />
            <Route path="/shipment/create">
              <TripScreen />
            </Route>
            <Route path="/shipment/trip/create">
              <CreateTripDetail />
            </Route>
          </AppProvider>
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
