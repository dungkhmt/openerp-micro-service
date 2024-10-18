import { LinearProgress } from "@mui/material";
import { Layout } from "../layout";
import { drawerWidth } from "../layout/sidebar/SideBar";
import { Suspense, useEffect } from "react";
import { Route, Switch, useLocation } from "react-router-dom";
import NotFound from "../views/errors/NotFound";
import DemoScreen from "../views/DemoScreen";

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

  useEffect(() => {
  }, [location.pathname]);

  return (
    <Layout>
      <Suspense fallback={<LinearProgress sx={styles.loadingProgress} />}>
        <Switch>
          <Route exact path="/" component={() => <h1>Welcome back !</h1>} />
          <Route
            exact path="/product/all" component={DemoScreen}
          />
          <Route
            exact path="/inventory" component={DemoScreen}
          />
          <Route component={NotFound} />
        </Switch>
      </Suspense>
    </Layout>
  );
}

export default MainAppRouter;
