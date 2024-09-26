import {
  Box,
  Chip,
  Collapse,
  ListItem,
  ListItemButton,
  ListItemIcon,
  Typography,
  styled,
} from "@mui/material";
import clsx from "clsx";
import PropTypes from "prop-types";
import { useLocation } from "react-router-dom";
import { useEffect } from "react";
import Icon from "../../../components/icon";
import { NavItems } from "./NavItems";

const hasActiveChild = (item, path) => {
  const { children } = item;

  if (!children) {
    return false;
  }

  for (const child of children) {
    if (child.children) {
      if (hasActiveChild(child, path)) {
        return true;
      }
    }
    const childPath = child.path;

    // Check if the child has a link and is active
    if (
      child &&
      childPath &&
      path &&
      (childPath === path || (path.includes(childPath) && childPath !== "/"))
    ) {
      return true;
    }
  }

  return false;
};

const MenuItemTextWrapper = styled(Box)(({ theme }) => ({
  width: "100%",
  display: "flex",
  alignItems: "center",
  gap: theme.spacing(2),
  justifyContent: "space-between",
  transition: "opacity .25s ease-in-out",
  overflow: "hidden",
}));

const NavGroup = (props) => {
  const {
    item,
    parent,
    navHover,
    navVisible,
    isSubToSub,
    groupActive,
    setGroupActive,
    collapsedNavWidth,
    currentActiveGroup,
    setCurrentActiveGroup,
    navCollapsed,
  } = props;

  // get path
  const { pathname } = useLocation();

  const handleGroupClick = () => {
    const openGroup = groupActive;
    if (openGroup.includes(item.title)) {
      openGroup.splice(openGroup.indexOf(item.title), 1);
    } else {
      openGroup.push(item.title);
    }
    setGroupActive([...openGroup]);
  };

  useEffect(() => {
    if (hasActiveChild(item, pathname)) {
      if (!groupActive.includes(item.title)) groupActive.push(item.title);
    } else {
      const index = groupActive.indexOf(item.title);
      if (index > -1) groupActive.splice(index, 1);
    }
    setGroupActive([...groupActive]);
    setCurrentActiveGroup([...groupActive]);

    if (navCollapsed && !navHover) {
      setGroupActive([]);
    }
  }, [pathname]);

  useEffect(() => {
    if (navCollapsed && !navHover) {
      setGroupActive([]);
    }

    if (
      (navCollapsed && navHover) ||
      (groupActive.length === 0 && !navCollapsed)
    ) {
      setGroupActive([...currentActiveGroup]);
    }
  }, [navCollapsed, navHover]);

  useEffect(() => {
    if (groupActive.length === 0 && !navCollapsed) {
      setGroupActive([]);
    }
  }, [navHover]);

  const icon = parent && !item.icon ? "mdi:circle-outline" : item.icon;

  const menuGroupCollapsedStyles =
    navCollapsed && !navHover ? { opacity: 0 } : { opacity: 1 };

  return (
    <ListItem
      disablePadding
      className="nav-group"
      onClick={handleGroupClick}
      sx={{ mt: 1.5, px: "0 !important", flexDirection: "column" }}
    >
      <ListItemButton
        className={clsx({
          "Mui-selected":
            groupActive.includes(item.title) ||
            currentActiveGroup.includes(item.title),
        })}
        sx={{
          py: 2.25,
          width: "100%",
          borderTopRightRadius: 100,
          borderBottomRightRadius: 100,
          transition: "padding-left .25s ease-in-out",
          pl: navCollapsed && !navHover ? (collapsedNavWidth - 24) / 8 : 5.5,
          pr:
            navCollapsed && !navHover
              ? ((collapsedNavWidth - 24) / 2 - 5) / 4
              : 3.5,
          "&.Mui-selected": {
            backgroundColor: "action.hover",
            "&:hover": {
              backgroundColor: "action.hover",
            },
          },
          "&.Mui-selected.Mui-focusVisible": {
            backgroundColor: "action.focus",
            "&:hover": {
              backgroundColor: "action.focus",
            },
          },
        }}
      >
        {isSubToSub ? null : (
          <ListItemIcon
            sx={{
              color: "text.primary",
              transition: "margin .25s ease-in-out",
              ...(parent && navCollapsed && !navHover ? {} : { mr: 2.5 }),
              ...(navCollapsed && !navHover ? { mr: 0 } : {}), // this condition should come after (parent && navCollapsed && !navHover) condition for proper styling
              ...(parent && item.children ? { ml: 1.25, mr: 3.75 } : {}),
            }}
          >
            <Icon icon={icon} {...(parent && { fontSize: "0.875rem" })} />
          </ListItemIcon>
        )}
        <MenuItemTextWrapper
          sx={{ ...menuGroupCollapsedStyles, ...(isSubToSub ? { ml: 9 } : {}) }}
        >
          <Typography noWrap>{item.title}</Typography>
          <Box
            className="menu-item-meta"
            sx={{
              display: "flex",
              alignItems: "center",
              "& svg": {
                color: "text.primary",
                transition: "transform .25s ease-in-out",
                ...(groupActive.includes(item.title) && {
                  transform: "rotate(90deg)",
                }),
              },
            }}
          >
            {item.badgeContent ? (
              <Chip
                label={item.badgeContent}
                color={item.badgeColor || "primary"}
                sx={{
                  mr: 1.5,
                  height: 20,
                  fontWeight: 500,
                  "& .MuiChip-label": { px: 1.5, textTransform: "capitalize" },
                }}
              />
            ) : null}
            <Icon icon="mdi:chevron-right" />
          </Box>
        </MenuItemTextWrapper>
      </ListItemButton>
      <Collapse
        component="ul"
        onClick={(e) => e.stopPropagation()}
        in={groupActive.includes(item.title)}
        sx={{
          pl: 0,
          width: "100%",
          ...menuGroupCollapsedStyles,
          transition: "all 0.25s ease-in-out",
        }}
      >
        <NavItems
          {...props}
          parent={item}
          navVisible={navVisible}
          items={item.children}
          isSubToSub={parent && item.children ? item : undefined}
        />
      </Collapse>
    </ListItem>
  );
};

NavGroup.propTypes = {
  item: PropTypes.object.isRequired,
  parent: PropTypes.object,
  navHover: PropTypes.bool,
  navVisible: PropTypes.bool,
  isSubToSub: PropTypes.object,
  groupActive: PropTypes.array,
  setGroupActive: PropTypes.func,
  collapsedNavWidth: PropTypes.number,
  currentActiveGroup: PropTypes.array,
  setCurrentActiveGroup: PropTypes.func,
  navigationBorderWidth: PropTypes.number,
  navCollapsed: PropTypes.bool,
};

export { NavGroup };
