import { SwipeableDrawer as MuiSwipeableDrawer, styled } from "@mui/material";
import PropTypes from "prop-types";

const SwipeableDrawer = styled(MuiSwipeableDrawer)({
  overflowX: "hidden",
  transition: "width .25s ease-in-out",
  "& ul": {
    listStyle: "none",
  },
  "& .MuiListItem-gutters": {
    paddingLeft: 4,
    paddingRight: 4,
  },
  "& .MuiDrawer-paper": {
    left: "unset",
    right: "unset",
    overflowX: "hidden",
    transition: "width .25s ease-in-out, box-shadow .25s ease-in-out",
  },
});

const Drawer = (props) => {
  const {
    hidden,
    children,
    navHover,
    navWidth,
    navVisible,
    setNavHover,
    setNavVisible,
    collapsedNavWidth,
    navCollapsed,
  } = props;

  let flag = true;

  const MobileDrawerProps = {
    open: navVisible,
    onOpen: () => setNavVisible(true),
    onClose: () => setNavVisible(false),
    ModalProps: {
      keepMounted: true,
    },
  };

  const DesktopDrawerProps = {
    open: true,
    onOpen: () => null,
    onClose: () => null,
    onMouseEnter: () => {
      if (flag) {
        setNavHover(true);
        flag = false;
      }
    },
    onMouseLeave: () => {
      if (navCollapsed) {
        setNavHover(false);
      }
    },
  };

  return (
    <SwipeableDrawer
      className="layout-vertical-nav"
      variant={hidden ? "temporary" : "permanent"}
      {...(hidden ? { ...MobileDrawerProps } : { ...DesktopDrawerProps })}
      PaperProps={{
        sx: {
          backgroundColor: "background.default",
          width: navCollapsed && !navHover ? collapsedNavWidth : navWidth,
          ...(!hidden && navCollapsed && navHover ? { boxShadow: 9 } : {}),
        },
      }}
      sx={{
        width: navCollapsed ? collapsedNavWidth : navWidth,
      }}
    >
      {children}
    </SwipeableDrawer>
  );
};

Drawer.propTypes = {
  children: PropTypes.node.isRequired,
  hidden: PropTypes.bool.isRequired,
  navHover: PropTypes.bool.isRequired,
  navWidth: PropTypes.number.isRequired,
  navVisible: PropTypes.bool.isRequired,
  setNavHover: PropTypes.func.isRequired,
  setNavVisible: PropTypes.func.isRequired,
  navMenuProps: PropTypes.object.isRequired,
  collapsedNavWidth: PropTypes.number.isRequired,
  navCollapsed: PropTypes.bool.isRequired,
};

export default Drawer;
