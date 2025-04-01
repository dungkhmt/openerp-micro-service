import Tabs from "@mui/material/Tabs";
import React from "react";
import { AntScrollButton } from "./AntScrollButton";
import { styled } from "@mui/material/styles";
// export const StyledTabs2 = withStyles({
//   root: {
//     borderBottom: "1px solid #e8e8e8",
//   },
//   indicator: {
//     display: "flex",
//     justifyContent: "center",
//     backgroundColor: "transparent",
//     "& > span": {
//       maxWidth: 40,
//       width: "100%",
//       backgroundColor: "#1890ff",
//     },
//   },
// })((props) => (
//   <Tabs
//     {...props}
//     ScrollButtonComponent={AntScrollButton}
//     TabIndicatorProps={{ children: <span /> }}
//   />
// ));

export const StyledTabs = styled((props) => (
  <Tabs
    {...props}
    ScrollButtonComponent={AntScrollButton}
    TabIndicatorProps={{ children: <span /> }}
  />
))(({ theme }) => ({
  borderBottom: "1px solid #e8e8e8",
  display: "flex",
  justifyContent: "center",
  backgroundColor: "transparent",
  "& > span": {
    maxWidth: 40,
    width: "100%",
    backgroundColor: "#1890ff",
  },
}));

