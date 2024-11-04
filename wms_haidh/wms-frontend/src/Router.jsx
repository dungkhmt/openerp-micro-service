import { LinearProgress } from "@mui/material";
import { Suspense } from "react";
import { Route, Routes } from "react-router-dom";
import MainAppRouter from "./routers/MainAppRouter";
import { routeState } from "./state/RouteState";

// const Register = lazy(() => import("../src/views/UserRegister/Register"));

function Router(props) {
  return (
    <Suspense
      fallback={
        <LinearProgress
          style={{
            position: "absolute",
            top: 0,
            width: "100%",
            zIndex: 1202,
          }}
        />
      }
    >
      <Routes>
        {/* Uncomment and adjust this line when Register is needed */}
        {/* <Route path="/user/register" element={<Register />} /> */}
        <Route
          path="*"
          element={<MainAppRouter />}
          // Since we're not passing props directly like before,
          // you'll need to handle currentRoute differently
          // if required in MainAppRouter.
        />
      </Routes>
    </Suspense>
  );
}

export default Router;
