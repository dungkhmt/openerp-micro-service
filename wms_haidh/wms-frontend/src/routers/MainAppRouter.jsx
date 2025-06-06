import { LinearProgress } from "@mui/material";
import { Layout } from "../layout";
import { drawerWidth } from "../layout/sidebar/SideBar";
import { Suspense } from "react";
import { Route, Routes } from "react-router-dom";
// import { useNotificationState } from "../state/NotificationState";
import NotFound from "../views/errors/NotFound";
import { useKeycloak } from "@react-keycloak/web";
import WarehouseManagerRouter from "./WarehouseManagerRouter";
import WarehouseStaffRouter from "./WarehouseStaffRouter";
import SaleManagerRouter from "./SaleManagerRouter";
import CustomerRouter from "./CustomerRouter";
import DeliveryManagerRouter from "./DeliveryManagerRouter";
import DeliveryPersonRouter from "./DeliveryPersonRouter";
import PurchaseManagerRouter from "./PurchaseManagerRouter";
import PurchaseStaffRouter from "./PurchaseStaffRouter";
import DirectorRouter from "./DirectorRouter";
import { SidebarProvider } from "../layout/SidebarContext";
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

function MainAppRouter() {
  // const location = useLocation();
  // const notificationState = useNotificationState();
  const { keycloak } = useKeycloak();

  // useEffect(() => {
  //   notificationState.open.set(false);
  // }, [location.pathname, keycloak]);

  const renderPrivateRoute = (Component) => {
    if (!keycloak.authenticated) {
      keycloak.login();
      return null;
    }
    return <Component />;
  };

  return (
    <SidebarProvider>
      <Layout>
        <Suspense fallback={<LinearProgress sx={styles.loadingProgress} />}>
          <Routes>
            <Route path="/" element={<div>Welcome back !</div>} />
            <Route path="/director/*" element={renderPrivateRoute(DirectorRouter)} />
            <Route path="/warehouse-manager/*" element={renderPrivateRoute(WarehouseManagerRouter)} />
            <Route path="/warehouse-staff/*" element={renderPrivateRoute(WarehouseStaffRouter)} />
            <Route path="/sale-manager/*" element={renderPrivateRoute(SaleManagerRouter)} />
            <Route path="/customer/*" element={renderPrivateRoute(CustomerRouter)} />
            <Route path="/delivery-manager/*" element={renderPrivateRoute(DeliveryManagerRouter)} />
            <Route path="/delivery-staff/*" element={renderPrivateRoute(DeliveryPersonRouter)} />
            <Route path="/purchase-staff/*" element={renderPrivateRoute(PurchaseStaffRouter)} />
            <Route path="/purchase-manager/*" element={renderPrivateRoute(PurchaseManagerRouter)} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </Layout>
    </SidebarProvider>
  );
}

export default MainAppRouter;
