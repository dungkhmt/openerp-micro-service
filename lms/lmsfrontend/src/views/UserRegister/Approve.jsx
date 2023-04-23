// import {Box, List, Typography} from "@material-ui/core";
// import randomColor from "randomcolor";
// import React, {useEffect, useState} from "react";
// import {request} from "../../api";
// import {ReactComponent as EmptyRegistrationListIcon} from "../../assets/icons/undraw_Forms_re_pkrt.svg";
// import RegistrationDetail from "../../component/userregister/RegistrationDetail";
//
// function NewApprove() {
//   const [rolesList, setRolesList] = useState(); // For rendering menu
//   const [registrations, setRegistrations] = useState();
//
//   useEffect(() => {
//     function getData() {
//       request("get", `/user/registration-list`, (res) => {
//         let registrations;
//         const rolesMap = {};
//
//         const data = res.data;
//         data.roles.forEach((role) => {
//           rolesMap[role.id] = role.name;
//         });
//
//         // Convert registered roles from string to array.
//         registrations = data.regists.map((registration) => ({
//           ...registration,
//           requestedRoleIds: registration.roles.split(","), // covert requested roles string to array
//           requestedRoleNames: registration.roles // convert requested roles from id to name to display in detail panel
//             .split(",")
//             .map((id) => rolesMap[id])
//             .join(", "),
//           avatarBgColor: randomColor({
//             luminosity: "dark",
//             hue: "random",
//           }),
//           // To expand detail panel in first loading.
//           // tableData: {
//           //   showDetailPanel: () => customDetailPanel(registration),
//           // },
//         }));
//
//         // changePageSize(registrations.length, tableRef);
//
//         setRegistrations(registrations);
//         setRolesList(
//           data.roles.sort((firstRole, secondRole) =>
//             firstRole.name.localeCompare(secondRole.name)
//           )
//         );
//       });
//     }
//
//     getData();
//   }, []);
//
//   // // Table.
//   // const tableRef = useRef(null);
//
//   // // Functions.
//   // // const notFirstClicked = new Set();
//   // const customDetailPanel = (rowData) => (
//   //   <RegistrationDetail data={rowData} rolesList={rolesList} />
//   // );
//
//   return (
//     <div style={{ maxWidth: 960, margin: "auto" }}>
//       <Typography variant="h5" style={{ marginBottom: 16 }}>
//         {registrations
//           ? `Đăng ký tài khoản (${registrations.length})`
//           : "Đăng ký tài khoản"}
//       </Typography>
//
//       <List aria-labelledby="nested-list-subheader">
//         {registrations ? (
//           registrations.length === 0 ? (
//             <Box
//               display="flex"
//               alignItems="center"
//               flexDirection="column"
//               pl={4}
//               pr={4}
//             >
//               <EmptyRegistrationListIcon width={360} height={225} />
//               <Typography style={{ textAlign: "center", marginTop: 24 }}>
//                 Khi người dùng đăng ký tài khoản, bạn có thể xem và phê duyệt
//                 chúng tại đây
//               </Typography>
//             </Box>
//           ) : (
//             registrations.map((registration) => (
//               <RegistrationDetail
//                 key={registration.id}
//                 data={registration}
//                 rolesList={rolesList}
//               />
//             ))
//           )
//         ) : (
//           Array.from(new Array(10)).map((_, index) => (
//             <RegistrationDetail
//               key={index}
//               data={undefined}
//               rolesList={undefined}
//             />
//           ))
//         )}
//       </List>
//     </div>
//   );
// }
//
// export default React.memo(NewApprove);
//
// // const formatDate = (strDate) => {
// //   let date = new Date(strDate);
//
// //   return `${date.getDate()} Tháng ${
// //     date.getMonth() + 1
// //   }, ${date.getFullYear()}`;
// // };
//
// // const cellStyles = { headerStyle: { padding: 8 }, cellStyle: { padding: 8 } };
// // const columns = [
// //   { filtering: false, field: "id", title: "Tên đăng nhập", ...cellStyles },
// //   {
// //     field: "fullName",
// //     title: "Tên đầy đủ",
// //     ...cellStyles,
// //   },
// //   {
// //     field: "email",
// //     title: "Email",
// //     ...cellStyles,
// //     render: (rowData) => (
// //       <Link
// //         style={{ wordBreak: "break-word", wordWrap: "break-word" }}
// //         href={`mailto:${rowData.email}`}
// //         onClick={(e) => e.stopPropagation()}
// //       >
// //         {rowData.email}
// //       </Link>
// //     ),
// //   },
// //   {
// //     sorting: false,
// //     filtering: false,
// //     field: "createdStamp",
// //     title: "Thời điểm đăng ký",
// //     render: (rowData) => formatDate(rowData.createdStamp),
// //     headerStyle: { padding: 8, width: 140 },
// //     cellStyle: { padding: 8, width: 140 },
// //   },
// // ];
//
// /* <MuiThemeProvider theme={theme}>
//         <MaterialTable
//           title=""
//           columns={columns}
//           tableRef={tableRef}
//           data={registrations || []}
//           localization={localization}
//           icons={tableIcons}
//           components={{
//             ...components,
//             Toolbar: (props) => (
//               <MTableToolbar
//                 {...props}
//                 searchFieldVariant="outlined"
//                 searchFieldStyle={{
//                   height: 40,
//                 }}
//               />
//             ),
//           }}
//           options={{
//             filtering: true,
//             search: false,
//             pageSize: 20,
//             debounceInterval: 500,
//             headerStyle: {
//               backgroundColor: "transparent",
//             },
//           }}
//           detailPanel={(rowData) => customDetailPanel(rowData)}
//           onRowClick={(event, rowData, togglePanel) => {
//             togglePanel();
//             // // Because in source code use toString() on detailPanel.
//             // if (notFirstClicked.has(rowData.id)) togglePanel();
//             // else {
//             //   togglePanel();
//             //   togglePanel();
//             //   notFirstClicked.add(rowData.id);
//             // }
//           }}
//         />
//       </MuiThemeProvider> */
