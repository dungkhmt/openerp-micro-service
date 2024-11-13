import { Icon, ListItemText } from "@mui/material";
import { menuIconMap } from "config/menuconfig";
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
    color: blackColor,
    "&.MuiListItem-button:hover": {
      backgroundColor: "rgba(200, 200, 200, 0.2)",
    },
  },
  whiteFont: (theme) => ({
    ...menuItemBaseStyle(theme).whiteFont,
  }),
  blackFont: (theme) => ({
    ...menuItemBaseStyle(theme).blackFont,
  }),
  menuItem: (theme) => ({
    ...menuItemBaseStyle(theme).menuItem,
    textDecoration: "none",

    "&:hover,&:focus,&:visited,&": {
      color: blackColor,
    },
  }),
  menuItemText: (theme) => ({
    ...menuItemBaseStyle(theme).menuItemText,
  }),
  menuItemIcon: (theme) => ({
    ...menuItemBaseStyle(theme).menuItemIcon,
  }),
  blue: {
    color: whiteColor,
    backgroundImage: "linear-gradient(98deg, rgb(156, 159, 160), rgb(25, 118, 210) 94%)",
    // backgroundColor: infoColor[0],
    boxShadow: "rgba(0, 0, 0, 0.2) 0px 3px 3px -2px, rgba(0, 0, 0, 0.14) 0px 3px 4px 0px, rgba(0, 0, 0, 0.12) 0px 1px 8px 0px",
    "&:hover,&:focus": {
      // backgroundColor: infoColor[0],
      boxShadow: "rgba(0, 0, 0, 0.2) 0px 3px 3px -2px, rgba(0, 0, 0, 0.14) 0px 3px 4px 0px, rgba(0, 0, 0, 0.12) 0px 1px 8px 0px",
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
      to={menuItem.path ? process.env.PUBLIC_URL + menuItem.path : undefined}
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
            ...(selected ? styles.whiteFont(theme) : styles.blackFont(theme)),
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
          ...(selected ? styles.whiteFont(theme) : styles.blackFont(theme)),

        })}
        style={{ paddingLeft: icon ? 0 : 54 }}
        disableTypography={true}
      />
    </ListItemLink>
  );
}

export default React.memo(MenuItem);
