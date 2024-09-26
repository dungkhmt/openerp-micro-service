import { LinearProgress } from "@mui/material";
import { Suspense } from "react";
import { Route, Routes as Routes_ } from "react-router-dom";
import MainAppRouter from "./routers/MainAppRouter";

function Routes() {
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
      <Routes_>
        <Route path="*" element={<MainAppRouter />} />
      </Routes_>
    </Suspense>
  );
}

export default Routes;
