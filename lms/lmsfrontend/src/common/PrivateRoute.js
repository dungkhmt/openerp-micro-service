import React from "react";
import {Redirect, useHistory} from "react-router";
import {Route} from "react-router-dom";
import {useAuthState} from "../state/AuthState";

function PrivateRoute({ component: Component, ...rest }) {
  const history = useHistory();
  const { isAuthenticated, isValidating } = useAuthState();

  if (isValidating.get()) {
    return null;
  } else {
    return (
      <Route
        {...rest}
        render={(props) =>
          isAuthenticated.get() ? (
            <Component {...props} />
          ) : (
            <Redirect
              to={{ pathname: "/", state: { from: history.location } }}
            />
          )
        }
      />
    );
  }
}

export default PrivateRoute;
