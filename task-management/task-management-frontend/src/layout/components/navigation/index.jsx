import { Box, List } from "@mui/material";
import { useState } from "react";
import PropTypes from "prop-types";
import Drawer from "./Drawer";
import { NavItems } from "./NavItems";

const Navigation = (props) => {
  const { navCollapsed } = props;

  const [navHover, setNavHover] = useState(false);
  const [groupActive, setGroupActive] = useState([]);
  const [currentActiveGroup, setCurrentActiveGroup] = useState([]);

  return (
    <Drawer {...props} navHover={navHover} setNavHover={setNavHover}>
      <Box sx={{ position: "relative", overflow: "hidden", mt: 12 }}>
        <Box
          sx={{
            height: "100%",
            overflowY: "auto",
            overflowX: "hidden",
            "&::-webkit-scrollbar": {
              display: "none",
            },
            scrollbarWidth: "none",
            msOverflowStyle: "none",
            pb: 5,
          }}
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
        </Box>
      </Box>
    </Drawer>
  );
};

Navigation.propTypes = {
  navCollapsed: PropTypes.bool,
};

export { Navigation };
