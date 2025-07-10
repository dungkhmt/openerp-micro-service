import { Icon, ListItemText } from "@mui/material";
import { menuIconMap } from "../../config/menuconfig.jsx";
import React from "react";
import { menuItemBaseStyle } from "./GroupMenuItem";
import ListItemLink from "./ListItemLink";

const infoColor = ["#00acc1", "#26c6da", "#00acc1", "#00d3ee"];
export const whiteColor = "#FFF";
export const blackColor = "#000";

export const hexToRgb = (input) => {
  input = input + "";
  input = input.replace("#", "");
  let hexRegex = /[0-9A-Fa-f]/g;

  if (!hexRegex.test(input) || (input.length !== 3 && input.length !== 6)) {
    throw new Error("input is not a valid hex color.");
  }

  if (input.length === 3) {
    let first = input[0];
    let second = input[1];
    let last = input[2];
    input = first + first + second + second + last + last;
  }

  input = input.toUpperCase();
  let first = input[0] + input[1];
  let second = input[2] + input[3];
  let last = input[4] + input[5];
  return (
    parseInt(first, 16) +
    ", " +
    parseInt(second, 16) +
    ", " +
    parseInt(last, 16)
  );
};

const styles = {
  selected: {
    color: whiteColor,
    "&.MuiListItem-button:hover": {
      backgroundColor: "rgba(200, 200, 200, 0.2)",
    },
  },
  whiteFont: (theme) => ({
    ...menuItemBaseStyle(theme).whiteFont,
  }),
  menuItem: (theme) => ({
    ...menuItemBaseStyle(theme).menuItem,
    textDecoration: "none",

    "&:hover,&:focus,&:visited,&": {
      color: whiteColor,
    },
  }),
  menuItemText: (theme) => ({
    ...menuItemBaseStyle(theme).menuItemText,
  }),
  menuItemIcon: (theme) => ({
    ...menuItemBaseStyle(theme).menuItemIcon,
  }),
  blue: {
    backgroundColor: infoColor[0],
    boxShadow:
      "0 12px 20px -10px rgba(" +
      hexToRgb(infoColor[0]) +
      ",.28), 0 4px 20px 0 rgba(" +
      hexToRgb("#000") +
      ",.12), 0 7px 8px -5px rgba(" +
      hexToRgb(infoColor[0]) +
      ",.2)",
    "&:hover,&:focus": {
      backgroundColor: infoColor[0],
      boxShadow:
        "0 12px 20px -10px rgba(" +
        hexToRgb(infoColor[0]) +
        ",.28), 0 4px 20px 0 rgba(" +
        hexToRgb("#000") +
        ",.12), 0 7px 8px -5px rgba(" +
        hexToRgb(infoColor[0]) +
        ",.2)",
    },
  },
};

function MenuItem(props) {
  const { color, menuItem, selected, menu, icon } = props;

  if (!menuItem.isPublic) {
    if (!menu?.has(menuItem.id)) return null;
  }

  return (
    <ListItemLink
      button
      onClick={menuItem.onClick}
      disableGutters={false}
      to={menuItem.path ? "" + menuItem.path : undefined}
      sx={(theme) => ({
        ...styles.menuItem(theme),
        ...(selected ? styles[color] : styles.selected),
      })}
      style={{ height: icon ? "auto" : 40 }}
    >
      {/* Icon */}
      {icon && (
        <Icon
          sx={(theme) => ({
            ...styles.menuItemIcon(theme),
            ...styles.whiteFont(theme),
          })}
          style={{
            paddingLeft: 3,
            marginRight: 30,
          }}
        >
          {menuIconMap.get(menuItem.icon)}
        </Icon>
      )}

      <ListItemText
        primary={menuItem.text}
        sx={(theme) => ({
          ...styles.menuItemText(theme),
          ...styles.whiteFont(theme),
        })}
        style={{ paddingLeft: icon ? 0 : 54 }}
        disableTypography={true}
      />
    </ListItemLink>
  );
}

export default React.memo(MenuItem);
