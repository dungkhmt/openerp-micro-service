// import RemoveCircleIcon from "@mui/icons-material/RemoveCircle";
// import {Box, Divider, List, ListItem, ListItemIcon, ListItemText, Menu, MenuItem, Typography,} from "@mui/material";
// import {grey, pink, red} from "@mui/material/colors";
// import TertiaryButton from "component/button/TertiaryButton";
// import React from "react";
// import {FcApproval} from "react-icons/fc";
// import SimpleBar from "simplebar-react";
// import "simplebar/dist/simplebar.min.css";
//
// const styles = {
//   grantedRole: {
//     position: "relative",
//     width: "100%",
//     pl: 2,
//     pr: 6.25,
//   },
//   grantedRolesList: {
//     "& li": {
//       "& svg": {
//         display: "none",
//       },
//     },
//     "&:hover": {
//       "& li": {
//         "& svg": {
//           display: "block",
//           "&:hover": {
//             color: red["A700"],
//           },
//         },
//       },
//     },
//   },
//   removeIcon: {
//     fontSize: 42,
//     color: pink[400],
//     position: "absolute",
//     right: 0,
//     top: "50%",
//     marginTop: -2.5,
//     p: 1,
//   },
//   role: {
//     borderRadius: 2,
//     mx: 1,
//     pl: 1,
//     "&:hover": {
//       background: grey[200],
//     },
//   },
//   listItemIcon: {
//     minWidth: 32,
//   },
//   listItem: { py: 0 },
// };
//
// function GrantRole({ grantedRoles, roles, setRoles }) {
//   // Menu.
//   const [anchorEl, setAnchorEl] = React.useState(null);
//
//   const handleClick = (event) => {
//     setAnchorEl(event.currentTarget);
//   };
//
//   const handleClose = () => {
//     setAnchorEl(null);
//   };
//
//   // Functions.
//   const onAddRole = (e) => {
//     let { roleId } = e.currentTarget.dataset;
//
//     const selectedRole = roles.find((role) => role.id === roleId);
//     selectedRole.granted = true;
//
//     setRoles([...roles]);
//
//     handleClose(e);
//   };
//
//   const onRemoveRole = (removedRole) => {
//     removedRole.granted = false;
//     setRoles([...roles]);
//   };
//
//   return grantedRoles ? (
//     <>
//       <Typography variant="h6">Phân quyền</Typography>
//       <Divider />
//       <List>
//         {grantedRoles.map((role) => (
//           <ListItem key={role} sx={styles.listItem}>
//             <ListItemIcon sx={styles.listItemIcon}>
//               <FcApproval size={24} />
//             </ListItemIcon>
//             <ListItemText primary={role} />
//           </ListItem>
//         ))}
//       </List>
//     </>
//   ) : (
//     <>
//       <Box width="100%" display="flex" mb="1rem">
//         <Typography component="span" variant="h6">
//           Phân quyền
//         </Typography>
//
//         <TertiaryButton
//           aria-controls="simple-menu"
//           aria-haspopup="true"
//           onClick={handleClick}
//           sx={{ ml: "auto" }}
//         >
//           Thêm quyền
//         </TertiaryButton>
//       </Box>
//       <Menu
//         id="simple-menu"
//         anchorEl={anchorEl}
//         keepMounted
//         open={Boolean(anchorEl)}
//         onClose={handleClose}
//         PaperProps={{
//           sx: {
//             borderRadius: 2,
//             boxShadow:
//               "0 12px 28px 0 rgba(0, 0, 0, 0.2), 0 2px 4px 0 rgba(0, 0, 0, 0.1), inset 0 0 0 1px rgba(255, 255, 255, 0.5)",
//           },
//         }}
//       >
//         <SimpleBar
//           style={{
//             width: 300,
//             height: 400,
//             overflowX: "hidden",
//             overscrollBehaviorY: "none", // To prevent tag <main> be scrolled when menu'scrollbar reach end
//           }}
//         >
//           <List disablePadding aria-label="roles list">
//             {roles?.map(
//               (role) =>
//                 !role.granted && (
//                   <MenuItem
//                     key={role.id}
//                     data-role-id={role.id}
//                     onClick={onAddRole}
//                     sx={styles.role}
//                   >
//                     <Typography variant="inherit" noWrap>
//                       {role.name}
//                     </Typography>
//                   </MenuItem>
//                 )
//             )}
//           </List>
//         </SimpleBar>
//       </Menu>
//       <Divider />
//       <List sx={styles.grantedRolesList}>
//         {roles?.map(
//           (role) =>
//             role.granted && (
//               <ListItem key={role.id} divider sx={styles.grantedRole}>
//                 <ListItemText>{role.name}</ListItemText>
//                 <RemoveCircleIcon
//                   onClick={() => onRemoveRole(role)}
//                   sx={styles.removeIcon}
//                 />
//               </ListItem>
//             )
//         )}
//       </List>
//     </>
//   );
// }
//
// export default GrantRole;
//
// /* <Button
//         variant="outlined"
//         color="primary"
//         ref={anchorRef}
//         aria-controls={open ? "menu-list-grow" : undefined}
//         aria-haspopup="true"
//         onClick={handleToggle}
//       >
//         Thêm quyền
//       </Button>
//        <Popper
//         open={open}
//         anchorEl={anchorRef.current}
//         role={undefined}
//         transition
//         style={{
//           backgroundColor: "#ffffff",
//         }}
//       >
//         {({ TransitionProps, placement }) => (
//           <Grow
//             {...TransitionProps}
//             style={{
//               transformOrigin:
//                 placement === "bottom" ? "center top" : "center bottom",
//             }}
//           >
//             <Paper>
//               <ClickAwayListener onClickAway={handleClose}>
//                 <SimpleBar
//                   style={{
//                     //   marginTop: 64,
//                     //   marginBottom: 16,
//                     //   position: "relative",
//                     width: 500,
//                     height: 400,
//                     //   zIndex: "4",
//                     overflowX: "hidden",
//                     overscrollBehaviorY: "none", // To prevent tag <main> be scrolled when menu'scrollbar reach end
//                   }}
//                 >
//                   <ThemeProvider theme={theme}>
//                     <MenuList
//                       autoFocusItem={open}
//                       id="menu-list-grow"
//                       onKeyDown={handleListKeyDown}
//                     >
//                       {roles?.map(
//                         (role) =>
//                           !role.granted && (
//                             <MenuItem
//                               data-role-id={role.id}
//                               onClick={onSelectRole}
//                             >
//                               {role.name}
//                             </MenuItem>
//                           )
//                       )}
//                     </MenuList>
//                   </ThemeProvider>
//                 </SimpleBar>
//               </ClickAwayListener>
//             </Paper>
//           </Grow>
//         )}
//       </Popper> */
