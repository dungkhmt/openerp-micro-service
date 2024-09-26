// import { useEffect, useState } from "react";
// import { shallowEqual, useDispatch, useSelector } from "react-redux";
// import {
//   getScrSecurInfoFailure,
//   getScrSecurInfoSuccess,
// } from "../../../action/Screen";
// import { request } from "../../../api";
// import BouncingBallsLoader from "../../../views/common/BouncingBallsLoader";
// import NotFound from "../../../views/errors/NotFound";
// import NotAuthorized from "../../common/NotAuthorzied";

// function withAsynchScreenSecurity(SecuredScreen, id) {
//   return function AsynchSecuredScreen(props) {
//     const dispatch = useDispatch();

//     const { fetched, requestSuccess, permissions } = useSelector(
//       (state) => state.screenSecurity,
//       shallowEqual
//     );

//     // For displaying Loading screen.
//     const [isRequesting, setIsRequesting] = useState(true);

//     // Functions.
//     const getViewPermissions = () => {
//       request(
//         "get",
//         `/screen-security`,
//         (res) => {
//           dispatch(getScrSecurInfoSuccess(new Set(res.data)));
//           setIsRequesting(false);
//         },
//         {
//           onError: (e) => {
//             dispatch(getScrSecurInfoFailure());
//             setIsRequesting(false);
//           },
//           rest: (e) => {},
//         }
//       );
//     };

//     useEffect(() => {
//       if (fetched && requestSuccess) {
//         setIsRequesting(false);
//       } else {
//         getViewPermissions();
//       }
//     }, []);

//     if (isRequesting) {
//       return <BouncingBallsLoader />;
//     } else {
//       if (requestSuccess) {
//         if (permissions.has(id)) {
//           return <SecuredScreen {...props} />;
//         } else {
//           return <NotAuthorized />;
//         }
//       } else {
//         return <NotFound />; //  Thay bang component khong the ket noi den server.
//       }
//     }
//   };
// }

export default withAsynchScreenSecurity;
