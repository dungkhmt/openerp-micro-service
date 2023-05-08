// import {GET_SCREEN_SECURITY_INFORMATION_FAILURE, GET_SCREEN_SECURITY_INFORMATION_SUCCESS,} from "../action/Screen";
//
// const initState = {
//   fetched: false,
//   requestSuccess: false,
//   permissions: new Set(),
// };
//
// const screenSecurity = (state = initState, action) => {
//   switch (action.type) {
//     case GET_SCREEN_SECURITY_INFORMATION_FAILURE:
//       return {
//         ...state,
//         fetched: true,
//         requestSuccess: false,
//       };
//     case GET_SCREEN_SECURITY_INFORMATION_SUCCESS:
//       return {
//         fetched: true,
//         requestSuccess: true,
//         permissions: action.permissions,
//       };
//     default:
//       return state;
//   }
// };
//
// export default screenSecurity;
