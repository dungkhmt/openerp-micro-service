import { useKeycloak } from "@react-keycloak/web";
import { useHistory } from "react-router";
import { Route } from "react-router-dom";

function PrivateRoute({ component: Component, ...rest }) {
  const history = useHistory();
  const { keycloak } = useKeycloak();

  return (
    <Route
      {...rest}
      render={(props) =>
        keycloak.authenticated ? (
          <Component {...props} />
        ) : (
          // (
          //   <Redirect
          //     to={{ pathname: "/", state: { from: history.location } }}
          //   />
          // )
          keycloak.login()
        )
      }
    />
  );
}

export default PrivateRoute;