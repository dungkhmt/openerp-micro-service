import { useKeycloak } from "@react-keycloak/web";
import { useEffect } from "react";
import { Outlet } from "react-router-dom";
import { useDispatch } from "react-redux";
import { fetchCategories } from "../store/category";
import { fetchPriorities } from "../store/priority";
import { fetchStatuses } from "../store/status";
import { fetchSkills } from "../store/skill";
import { fetchUser } from "../store/my-profile";

function PrivateRoute() {
  const { keycloak } = useKeycloak();

  if (!keycloak.authenticated) {
    keycloak.login();
  }

  const dispatch = useDispatch();

  useEffect(() => {
    const fetchData = () =>
      Promise.all([
        dispatch(fetchStatuses()),
        dispatch(fetchCategories()),
        dispatch(fetchPriorities()),
        dispatch(fetchSkills()),
        dispatch(fetchUser()),
      ]);

    fetchData();
  }, [dispatch]);

  return <Outlet />;
}

export default PrivateRoute;
