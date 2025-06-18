import { useKeycloak } from "@react-keycloak/web";
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { Outlet } from "react-router-dom";
import { fetchOrganizationData } from "../store/utils/organizationContext";
import { useSelector } from "react-redux";
import { CircularProgressLoading } from "../components/common/loading/CircularProgressLoading";

function PrivateRoute() {
  const { keycloak } = useKeycloak();

  if (!keycloak.authenticated) {
    keycloak.login();
  }

  const dispatch = useDispatch();
  const { currentOrganization } = useSelector((state) => state.organization);

  useEffect(() => {
    if (currentOrganization?.id) {
      fetchOrganizationData(dispatch, currentOrganization.id);
    }
  }, [dispatch, currentOrganization]);

  if (!keycloak.authenticated || !currentOrganization?.id)
    return <CircularProgressLoading />;

  return <Outlet />;
}

export default PrivateRoute;
