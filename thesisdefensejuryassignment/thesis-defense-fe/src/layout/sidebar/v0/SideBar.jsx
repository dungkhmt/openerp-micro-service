// import Drawer from "@material-ui/core/Drawer";
// import List from "@material-ui/core/List";
// import makeStyles from "@material-ui/core/styles/makeStyles";
// import classNames from "classnames";
// import PropTypes from "prop-types";
// import React from "react";
// import {useSelector} from "react-redux";
// import SimpleBar from "simplebar-react";
// import "simplebar/dist/simplebar.min.css";
// import {drawerWidth} from "../assets/jss/material-dashboard-react";
// import styles from "../assets/jss/material-dashboard-react/components/sidebarStyle";
// import {MENU_LIST} from "../config/menuconfig";
// import MenuItem from "./MenuItem";
//
// const useStyles = makeStyles((theme) => ({
//   root: {
//     display: "flex",
//   },
//   menu: {
//     paddingTop: theme.spacing(10),
//   },
//   largeIcon: {
//     width: 50,
//     height: 50,
//   },
//   nested: {
//     paddingLeft: theme.spacing(4),
//   },
//   drawerPaper: {
//     border: "none",
//     // boxShadow: `2px 0px 1px -1px rgb(0 0 0 / 20%),
//     //   1px 0px 1px 0px rgb(0 0 0 / 14%),
//     //   1px 0px 3px 0px rgb(0 0 0 / 12%)`,
//   },
//   drawer: {
//     width: drawerWidth,
//     flexShrink: 0,
//     whiteSpace: "nowrap",
//   },
//   drawerOpen: {
//     width: drawerWidth,
//     transition: theme.transitions.create("width", {
//       easing: theme.transitions.easing.sharp,
//       duration: theme.transitions.duration.enteringScreen,
//     }),
//   },
//   drawerClose: {
//     transition: theme.transitions.create("width", {
//       easing: theme.transitions.easing.sharp,
//       duration: theme.transitions.duration.leavingScreen,
//     }),
//     width: theme.spacing(7) + 1,
//     [theme.breakpoints.up("sm")]: {
//       width: theme.spacing(9) + 1,
//     },
//   },
// }));
//
// export default function SideBar(props) {
//   const assetClasses = makeStyles(styles)();
//   const classes = useStyles();
//
//   const { open, image, color: bgColor } = props;
//   const selectedMenu = useSelector((state) => state.menu.selectedFunction);
//
//   return (
//     <Drawer
//       variant="permanent"
//       anchor="left"
//       open={open}
//       className={classNames(classes.drawer, {
//         [classes.drawerOpen]: open,
//         [classes.drawerClose]: !open,
//       })}
//       classes={{
//         paper: classNames(classes.drawerPaper, {
//           [classes.drawerOpen]: open,
//           [classes.drawerClose]: !open,
//         }),
//       }}
//     >
//       <div
//         className={classNames(assetClasses.sidebarWrapper, {
//           [classes.drawerOpen]: open,
//           [classes.drawerClose]: !open,
//         })}
//       >
//         <SimpleBar
//           style={{
//             height: "100%",
//             overflowX: "hidden",
//             overscrollBehaviorY: "none", // To prevent tag <main> be scrolled when menu'scrollbar reach end
//           }}
//         >
//           <List>
//             {MENU_LIST.map((config) => (
//               <MenuItem
//                 key={config.text}
//                 config={config}
//                 color={bgColor}
//                 open={open}
//                 selectedMenu={selectedMenu}
//               />
//             ))}
//           </List>
//         </SimpleBar>
//       </div>
//       {image !== undefined ? (
//         <div
//           className={classNames(assetClasses.background, {
//             [classes.drawerOpen]: open,
//             [classes.drawerClose]: !open,
//           })}
//           style={{ backgroundImage: "url(" + image + ")" }}
//         />
//       ) : null}
//     </Drawer>
//   );
// }
//
// SideBar.propTypes = {
//   bgColor: PropTypes.oneOf(["purple", "blue", "green", "orange", "red"]),
//   image: PropTypes.string,
//   open: PropTypes.bool,
// };
