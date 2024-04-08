import { useKeycloak } from "@react-keycloak/web";
import { useNavigate } from "react-router";
import { Route, Outlet } from "react-router-dom";

function PrivateRoute({ component: Component, ...rest }) {
  const navigate = useNavigate();
  const { keycloak } = useKeycloak();
  if (!keycloak.authenticated) {
    keycloak.login();
  }
  return <Outlet />;
}

export default PrivateRoute;
