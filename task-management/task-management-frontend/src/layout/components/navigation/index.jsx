import { Box, List, styled } from "@mui/material";
import { useState, useRef } from "react";
import PerfectScrollbar from "react-perfect-scrollbar";
import PropTypes from "prop-types";
import { hexToRGBA } from "../../../components/utils/hex-to-rgba";
import Drawer from "./Drawer";
import { NavItems } from "./NavItems";

const StyledBoxForShadow = styled(Box)(({ theme }) => ({
  top: 60,
  left: -8,
  zIndex: 2,
  opacity: 0,
  position: "absolute",
  pointerEvents: "none",
  width: "calc(100% + 15px)",
  height: theme.mixins.toolbar.minHeight,
  transition: "opacity .15s ease-in-out",
  background: `linear-gradient(${
    theme.palette.background.default
  } 95%,${hexToRGBA(theme.palette.background.default, 0.85)} 30%,${hexToRGBA(
    theme.palette.background.default,
    0.5
  )} 65%,${hexToRGBA(theme.palette.background.default, 0.3)} 75%,transparent)`,
  "&.scrolled": {
    opacity: 1,
  },
}));

const Navigation = (props) => {
  const { hidden, navCollapsed } = props;

  const [navHover, setNavHover] = useState(false);
  const [groupActive, setGroupActive] = useState([]);
  const [currentActiveGroup, setCurrentActiveGroup] = useState([]);

  const shadowRef = useRef(null);

  const handleInfiniteScroll = (ref) => {
    if (ref) {
      ref._getBoundingClientRect = ref.getBoundingClientRect;

      ref.getBoundingClientRect = () => {
        const original = ref._getBoundingClientRect();

        return { ...original, height: Math.floor(original.height) };
      };
    }
  };

  const scrollMenu = (container) => {
    if (!beforeNavMenuContent) {
      container = hidden ? container.target : container;
      if (shadowRef && container.scrollTop > 0) {
        if (!shadowRef.current.classList.contains("scrolled")) {
          shadowRef.current.classList.add("scrolled");
        }
      } else {
        shadowRef.current.classList.remove("scrolled");
      }
    }
  };

  const ScrollWrapper = hidden ? Box : PerfectScrollbar;

  return (
    <Drawer {...props} navHover={navHover} setNavHover={setNavHover}>
      <StyledBoxForShadow ref={shadowRef} />
      <Box sx={{ position: "relative", overflow: "hidden", mt: 12 }}>
        <ScrollWrapper
          {...(hidden
            ? {
                onScroll: (container) => scrollMenu(container),
                sx: { height: "100%", overflowY: "auto", overflowX: "hidden" },
              }
            : {
                options: { wheelPropagation: false },
                onScrollY: (container) => scrollMenu(container),
                containerRef: (ref) => handleInfiniteScroll(ref),
              })}
        >
          <List
            className="nav-items"
            sx={{
              pt: 0,
              transition: "padding .25s ease",
              "& > :first-child": { mt: "0" },
              pr: !navCollapsed || (navCollapsed && navHover) ? 4.5 : 1.25,
            }}
          >
            <NavItems
              navHover={navHover}
              groupActive={groupActive}
              setGroupActive={setGroupActive}
              currentActiveGroup={currentActiveGroup}
              setCurrentActiveGroup={setCurrentActiveGroup}
              {...props}
            />
          </List>
        </ScrollWrapper>
      </Box>
    </Drawer>
  );
};

Navigation.propTypes = {
  hidden: PropTypes.bool,
  navCollapsed: PropTypes.bool,
};

export { Navigation };