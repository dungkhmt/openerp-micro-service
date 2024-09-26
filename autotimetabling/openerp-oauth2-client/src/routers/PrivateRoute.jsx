import { useKeycloak } from "@react-keycloak/web";
import { useHistory } from "react-router";
import { Route } from "react-router-dom";

/**
 * PrivateRoute component checks if the user is authenticated using
 * Keycloak and renders the component if true, otherwise redirects to the login page.
 * @returns The `PrivateRoute` component is being returned.
 */
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
