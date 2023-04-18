import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {authGet} from "../api";
import Loading from "./common/Loading";
import NotAuthorized from "./common/NotAuthorzied";

function withScreenSecurity(SecurityComponet, screenName, viewError) {
  return function ScreenSecurityComponent({ ...props }) {
    const token = useSelector((state) => state.auth.token);
    const dispatch = useDispatch();
    const [isChecking, setIsChecking] = useState(true);
    const [isAuthorized, setIsAuthorized] = useState(false);
    console.log(SecurityComponet.name);
    useEffect(() => {
      setIsChecking(true);
      authGet(
        dispatch,
        token,
        "/check-authority" + "?applicationId=" + screenName
      ).then(
        (res) => {
          console.log(res);
          setIsChecking(false);
          if (res.result === "INCLUDED") setIsAuthorized(true);
        },
        (error) => {
          console.log("error");
          setIsChecking(false);
        }
      );
    }, []);
    if (isChecking) return <Loading />;
    else if (isAuthorized) return <SecurityComponet {...props} />;
    else if (viewError) return <NotAuthorized />;
    else return "";
  };
}
export default withScreenSecurity;
