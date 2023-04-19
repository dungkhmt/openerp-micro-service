import {Breadcrumbs, Link, Typography} from "@material-ui/core";
import React from "react";
import {useSelector} from "react-redux";

export function LayoutBreadcrumbs(props) {
  const selectedFunction = useSelector((state) => state.menu.selectedFunction);

  let bc1 = "";
  let bc2 = "";
  let bc3 = "";
  if (selectedFunction !== null) {
    bc1 = (
      <Typography color="textPrimary">
        {/* {menuIconMap.get(selectedFunction.icon)} */}
        {selectedFunction.text}
      </Typography>
    );
    if (
      selectedFunction.parent !== null &&
      selectedFunction.parent !== undefined
    ) {
      bc2 = (
        <Link color="inherit">
          {/* {menuIconMap.get(selectedFunction.parent.icon)} */}
          {selectedFunction.parent.text}
        </Link>
      );
      if (
        selectedFunction.parent.parent !== null &&
        selectedFunction.parent.parent !== undefined
      ) {
        bc3 = (
          <Link color="inherit">
            {/* {menuIconMap.get(selectedFunction.parent.parent.icon)} */}
            {selectedFunction.parent.parent.text}
          </Link>
        );
      }
    }
  } else return "";
  return (
    <Breadcrumbs aria-label="breadcrumb">
      <Link color="inherit" href="/">
        Trang chủ
      </Link>
      {bc3}
      {bc2}
      {bc1}
    </Breadcrumbs>
  );
}
