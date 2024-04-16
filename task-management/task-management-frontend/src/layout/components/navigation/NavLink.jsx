import PropTypes from "prop-types";
import { useLocation, Link } from "react-router-dom";
import {
  styled,
  Chip,
  ListItem,
  Typography,
  Box,
  ListItemIcon,
  ListItemButton,
} from "@mui/material";
import Icon from "../../../components/icon";

const handleURLQueries = (location, path) => {
  const searchParams = new URLSearchParams(location.search);

  if (Array.from(searchParams.keys()).length && path) {
    return (
      location.pathname.includes(path) &&
      location.search.includes(
        searchParams.get(Array.from(searchParams.keys())[0])
      ) &&
      path !== "/"
    );
  }

  return false;
};

const MenuNavLink = styled(ListItemButton)(({ theme }) => ({
  width: "100%",
  borderTopRightRadius: 100,
  borderBottomRightRadius: 100,
  color: theme.palette.text.primary,
  transition: "padding-left .25s ease-in-out",
  "&.active": {
    "&, &:hover": {
      boxShadow: theme.shadows[3],
      backgroundImage: `linear-gradient(98deg, ${theme.palette.customColors.primaryGradient}, ${theme.palette.primary.main} 94%)`,
    },
    "& .MuiTypography-root, & .MuiListItemIcon-root": {
      color: `${theme.palette.common.white} !important`,
    },
  },
}));

const MenuItemTextMetaWrapper = styled(Box)(({ theme }) => ({
  width: "100%",
  display: "flex",
  alignItems: "center",
  gap: theme.spacing(2),
  justifyContent: "space-between",
  transition: "opacity .25s ease-in-out",
  overflow: "hidden",
}));

const NavLink = ({
  item,
  parent,
  navHover,
  navVisible,
  isSubToSub,
  collapsedNavWidth,
  toggleNavVisibility,
  navCollapsed,
}) => {
  const location = useLocation();

  const icon = parent && !item.icon ? "mdi:circle-outline" : item.icon;

  const isNavLinkActive = () => {
    return (
      location.pathname === item.path || handleURLQueries(location, item.path)
    );
  };

  return (
    <ListItem
      disablePadding
      className="nav-link"
      disabled={item.disabled}
      sx={{ mt: 1, px: "0 !important" }}
    >
      <MenuNavLink
        component={Link}
        {...(item.disabled && { tabIndex: -1 })}
        className={isNavLinkActive() ? "active" : ""}
        to={item.path === undefined ? "/" : `${item.path}`}
        {...(item.openInNewTab ? { target: "_blank" } : null)}
        onClick={(e) => {
          if (item.path === undefined) {
            e.preventDefault();
            e.stopPropagation();
          }
          if (navVisible) {
            toggleNavVisibility();
          }
        }}
        sx={{
          py: 2.25,
          ...(item.disabled
            ? { pointerEvents: "none" }
            : { cursor: "pointer" }),
          pl: navCollapsed && !navHover ? (collapsedNavWidth - 24) / 8 : 5.5,
          pr:
            navCollapsed && !navHover
              ? ((collapsedNavWidth - 24) / 2 - 5) / 4
              : 3.5,
        }}
      >
        {isSubToSub ? null : (
          <ListItemIcon
            sx={{
              color: "text.primary",
              transition: "margin .25s ease-in-out",
              ...(navCollapsed && !navHover ? { mr: 0 } : { mr: 2.5 }),
              ...(parent ? { ml: 1.25, mr: 3.75 } : {}), // This line should be after (navCollapsed && !navHover) condition for proper styling
              "& svg": {
                fontSize: "0.875rem",
                ...(!parent ? { fontSize: "1.5rem" } : {}),
                ...(parent && item.icon ? { fontSize: "0.875rem" } : {}),
              },
            }}
          >
            <Icon icon={icon} />
          </ListItemIcon>
        )}

        <MenuItemTextMetaWrapper
          sx={{
            ...(isSubToSub ? { ml: 9 } : {}),
            ...(navCollapsed && !navHover ? { opacity: 0 } : { opacity: 1 }),
          }}
        >
          <Typography noWrap>{item.title}</Typography>
          {item.badgeContent ? (
            <Chip
              label={item.badgeContent}
              color={item.badgeColor || "primary"}
              sx={{
                height: 20,
                fontWeight: 500,
                "& .MuiChip-label": { px: 1.5, textTransform: "capitalize" },
              }}
            />
          ) : null}
        </MenuItemTextMetaWrapper>
      </MenuNavLink>
    </ListItem>
  );
};

NavLink.propTypes = {
  item: PropTypes.object.isRequired,
  parent: PropTypes.bool,
  navHover: PropTypes.bool,
  settings: PropTypes.object,
  navVisible: PropTypes.bool,
  isSubToSub: PropTypes.bool,
  collapsedNavWidth: PropTypes.number,
  toggleNavVisibility: PropTypes.func,
  navCollapsed: PropTypes.bool,
};

export { NavLink };
