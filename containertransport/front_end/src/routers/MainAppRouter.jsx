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
          <PrivateRoute component={TruckScreen} exact path="/truck" />
          <PrivateRoute component={FacilityScreen} exact path="/facility" />
          <PrivateRoute component={FacilityDetail} exact path="/facility/detail/:facilityId" />
          <PrivateRoute component={OrderScreen} exact path="/order" />
          <PrivateRoute component={ContainerScreen} exact path="/container" />
          <Route component={TrailerScreen} exact path="/trailer" />
          <AppProvider>
            <Route component={ShipmentScreen} exact path="/shipment" />
            <Route component={ShipmentDetail} exact path="/shipment/detail/:shipmentId" />
            <Route component={TripDetail} exact path="/shipment/trip/detail/:shipmentId/:tripId" />
            <Route component={CreateTripDetail} path="/shipment/trip/create/:shipmentId" />
          </AppProvider>
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
