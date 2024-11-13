import {withStyles} from "@material-ui/core/styles";
import Tabs from "@material-ui/core/Tabs";
import React from "react";
import {AntScrollButton} from "./AntScrollButton";

export const StyledTabs = withStyles({
  root: {
    borderBottom: "1px solid #e8e8e8",
  },
  indicator: {
    display: "flex",
    justifyContent: "center",
    backgroundColor: "transparent",
    "& > span": {
      maxWidth: 40,
      width: "100%",
      backgroundColor: "#1890ff",
    },
  },
})((props) => (
  <Tabs
    {...props}
    ScrollButtonComponent={AntScrollButton}
    TabIndicatorProps={{ children: <span /> }}
  />
));
