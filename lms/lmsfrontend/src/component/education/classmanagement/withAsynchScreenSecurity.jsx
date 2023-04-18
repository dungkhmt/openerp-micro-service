import React, {useEffect, useState} from "react";
import {shallowEqual, useDispatch, useSelector} from "react-redux";
import {useHistory} from "react-router";
import {getScrSecurInfoFailure, getScrSecurInfoSuccess,} from "../../../action/Screen";
import {request} from "../../../api";
import BouncingBallsLoader from "../../../views/common/BouncingBallsLoader";
import NotFound from "../../../views/errors/NotFound";
import NotAuthorized from "../../common/NotAuthorzied";

function withAsynchScreenSecurity(SecuredScreen, id) {
  return function AsynchSecuredScreen(props) {
    console.log("RENDER");
    const history = useHistory();
    const dispatch = useDispatch();
    const token = useSelector((state) => state.auth.token);

    const { fetched, requestSuccess, permissions } = useSelector(
      (state) => state.screenSecurity,
      shallowEqual
    );

    // For displaying Loading screen.
    const [isRequesting, setIsRequesting] = useState(true);

    // Functions.
    const getViewPermissions = () => {
      request(
        // token,
        // history,
        "get",
        `/screen-security`,
        (res) => {
          dispatch(getScrSecurInfoSuccess(new Set(res.data)));
          setIsRequesting(false);
        },
        {
          onError: (e) => {
            dispatch(getScrSecurInfoFailure());
            setIsRequesting(false);
          },
          rest: (e) => {},
        }
      );
    };

    useEffect(() => {
      if (fetched && requestSuccess) {
        setIsRequesting(false);
      } else {
        getViewPermissions();
      }
    }, []);

    if (isRequesting) {
      return <BouncingBallsLoader />;
    } else {
      if (requestSuccess) {
        if (permissions.has(id)) {
          return <SecuredScreen {...props} />;
        } else {
          return <NotAuthorized />;
        }
      } else {
        return <NotFound />; //  Thay bang component khong the ket noi den server.
      }
    }
  };
}

export default withAsynchScreenSecurity;
