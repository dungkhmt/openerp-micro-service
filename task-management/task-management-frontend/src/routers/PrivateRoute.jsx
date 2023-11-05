import { useKeycloak } from "@react-keycloak/web";
import { Outlet } from "react-router-dom";

function PrivateRoute() {
  const { keycloak } = useKeycloak();

  if (!keycloak.authenticated) {
    keycloak.login();
  }

  return <Outlet />;
}

export default PrivateRoute;
