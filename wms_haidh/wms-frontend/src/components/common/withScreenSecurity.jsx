import { LinearProgress } from "@mui/material";
import { request } from "../../api";
import { useEffect, useState } from "react";
import NotAuthorized from "./NotAuthorized";

/**
 * The function withScreenSecurity is a higher-order component that adds screen security to a component
 * by checking if the user has authorization to view it.
 * @param SecuredComponent - The component that needs to be secured with screen authorization.
 * @param id - The `id` parameter is a string that represents the unique identifier of the screen or
 * component that needs to be secured. It is used to make a request to the server to check if the user
 * has the necessary authorization to view the screen.
 * @param viewError - A boolean value indicating whether to display a "Not Authorized" error message
 * when the user does not have permission to view the secured component.
 * @returns The function `withScreenSecurity` is being returned.
 */
function withScreenSecurity(SecuredComponent, id, viewError) {
  return function ScreenSecurityComponent({ ...props }) {
    const [checking, setChecking] = useState(true);
    const [screenAuthorization, setScreenAuthorization] = useState();

    useEffect(() => {
      setChecking(true);

      request(
        "get",
        `/entity-authorization/${id}`,
        (res) => {
          setScreenAuthorization(new Set(res.data));
          setChecking(false);
        },
        {
          onError: (e) => {
            setChecking(false);
            console.log(e);
          },
        }
      );
    }, []);

    if (checking)
      return (
        <LinearProgress
          style={{
            position: "absolute",
            top: 0,
            left: -1, // when use 0, the progress bar be blurred
            width: "100%",
            zIndex: 1202,
          }}
        />
      );
    else if (screenAuthorization.has(`${id}.VIEW`))
      return (
        <SecuredComponent
          {...props}
          screenAuthorization={screenAuthorization}
        />
      );
    else if (viewError) return <NotAuthorized />;
    else return "";
  };
}

export default withScreenSecurity;
