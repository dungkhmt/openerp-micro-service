import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import {
  Collapse,
  Icon,
  List,
  ListItemButton,
  ListItemText,
} from "@mui/material";
import { whiteColor } from "../../assets/jss/material-dashboard-react";
import { menuIconMap } from "../../config/menuconfig";
import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { useMenuState } from "../../state/MenuState";
import MenuItem, { hexToRgb } from "./MenuItem";
import PropTypes from "prop-types";

export const menuItemBaseStyle = (theme) => ({
  whiteFont: {
    color: "#FFF",
  },
  menuItem: {
    margin: "10px 15px 0 12px",
    padding: "10px",
    width: "auto",
    minWidth: 50,
    transition: "all 300ms linear",
    borderRadius: "3px",
    position: "relative",
    backgroundColor: "transparent",
    // lineHeight: "1.5em",
  },
  menuItemIcon: {
    width: "24px",
    height: "30px",
    fontSize: "24px",
    lineHeight: "30px",
    float: "left",
    marginRight: "15px",
    textAlign: "center",
    // verticalAlign: "middle",
    display: "flex",
    alignItems: "center",
    color: "rgba(" + hexToRgb(whiteColor) + ", 0.8)",
  },
  menuItemText: {
    fontWeight: theme.typography.fontWeightMedium,
    margin: "0",
    lineHeight: "30px",
    fontSize: "14px",
  },
});

const styles = {
  childSelected: {
    "&.MuiListItem-button": {
      backgroundColor: "rgba(200, 200, 200, 0.2)",
    },
  },
  iconExpand: { transform: "rotate(-180deg)", transition: "0.3s" },
  iconCollapse: { transition: "0.3s" },
  whiteFont: (theme) => ({
    ...menuItemBaseStyle(theme).whiteFont,
  }),
  menuItemIcon: (theme) => ({
    ...menuItemBaseStyle(theme).menuItemIcon,
  }),
  menuItemText: (theme) => ({
    ...menuItemBaseStyle(theme).menuItemText,
  }),
  menuItem: (theme) => ({
    ...menuItemBaseStyle(theme).menuItem,
    color: whiteColor,

    "&.MuiListItem-button:hover": {
      backgroundColor: "rgba(200, 200, 200, 0.2)",
    },
  }),
};

const activeRoute = (route) => {
  if (route === "/" || route === "") {
    // access http://localhost:3000/ or http://localhost:3000, location is always http://localhost:3000/
    return window.location.pathname === `/`;
  } else {
    return window.location.pathname.startsWith(route);
  }
};

/**
 * @param {string} str
 * @param {Set<string>} set
 */
const findFirstElementStartingWith = (str, set) => {
  return [...set].find((element) => element.startsWith(str));
};

function GroupMenuItem(props) {
  const { color, group } = props;
  const location = useLocation();

  //
  const menuState = useMenuState();
  const permittedFunctions = menuState.permittedFunctions.get({
    noproxy: true,
  });

  //
  const [expanded, setExpanded] = useState(false);
  const [hasChildSelected, setHasChildSelected] = useState(false);
  const [selected, setSelected] = useState(
    group.child.map((menuItem) => activeRoute(menuItem.path))
  );

  //
  const checkSelected = () => {
    setHasChildSelected(false);
    setSelected(
      group.child.map((menuItem) => {
        const selected = activeRoute(menuItem.path);

        if (selected) {
          setHasChildSelected(true);
        }
        return selected;
      })
    );
  };

  useEffect(() => {
    checkSelected();
  }, [location.pathname]);

  if (group.child) {
    let hasPublicChild = group.child.find(
      (childMenuItem) => childMenuItem.isPublic
    );

    if (!hasPublicChild) {
      let hasAuthorizedPrivateChild = findFirstElementStartingWith(
        group.id,
        permittedFunctions
      );
      if (!hasAuthorizedPrivateChild) {
        return null;
      }
    }
  }

  return (
    <li>
      <ListItemButton
        key={group.text}
        sx={(theme) => ({
          ...styles.menuItem(theme),
          ...(hasChildSelected ? styles.childSelected : {}),
        })}
        onClick={() => setExpanded(!expanded)}
      >
        {/* Icon */}
        <Icon
          sx={(theme) => ({
            ...styles.menuItemIcon(theme),
            ...styles.whiteFont(theme),
          })}
          style={{
            marginLeft: 3,
            marginRight: 27,
          }}
        >
          {menuIconMap.get(group.icon)}
        </Icon>

        {/* Label */}
        <ListItemText
          primary={group.text}
          sx={(theme) => ({
            ...styles.menuItemText(theme),
            ...styles.whiteFont(theme),

            // limited lines text
            whiteSpace: "nowrap",
            overflow: "hidden",
            textOverflow: "ellipsis",
          })}
          disableTypography={true}
        />

        <Icon
          sx={(theme) => ({
            ...styles.menuItemIcon(theme),
            ...styles.whiteFont(theme),
            ...(expanded ? styles.iconExpand : styles.iconCollapse),
          })}
          style={{
            marginRight: 0,
            marginLeft: 6,
          }}
        >
          <ArrowDropDownIcon />
        </Icon>
      </ListItemButton>
      <Collapse in={expanded} timeout="auto">
        <List disablePadding>
          {group.child.map((childMenuItem, index) => (
            <MenuItem
              key={childMenuItem.text}
              menuItem={childMenuItem}
              color={color}
              selected={selected[index]}
              menu={permittedFunctions}
            />
          ))}
        </List>
      </Collapse>
    </li>
  );
}

GroupMenuItem.propTypes = {
  color: PropTypes.string,
  group: PropTypes.object,
};

export default GroupMenuItem;
