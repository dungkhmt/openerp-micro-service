import { Box, Drawer, IconButton, Typography } from "@mui/material";
import PropTypes from "prop-types";
import Icon from "../../../components/icon";

const drawerWidth = 400;

const AddEventSidebar = (props) => {
  const { addEventSidebarOpen, handleAddEventSidebarToggle } = props;

  const handleSidebarClose = async () => {
    handleAddEventSidebarToggle();
  };

  return (
    <Drawer
      anchor="right"
      open={addEventSidebarOpen}
      onClose={handleSidebarClose}
      ModalProps={{ keepMounted: true }}
      sx={{
        "& .MuiDrawer-paper": { width: ["100%", drawerWidth] },
        zIndex: 3000,
      }}
    >
      <Box
        className="sidebar-header"
        sx={{
          display: "flex",
          justifyContent: "space-between",
          backgroundColor: "background.default",
          p: (theme) => theme.spacing(3, 3.255, 3, 5.255),
        }}
      >
        <Typography variant="h6">Add event</Typography>
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <IconButton
            size="small"
            onClick={handleSidebarClose}
            sx={{ color: "text.primary" }}
          >
            <Icon icon="mdi:close" fontSize={20} />
          </IconButton>
        </Box>
      </Box>
      <Box className="sidebar-body" sx={{ p: (theme) => theme.spacing(5, 6) }}>
        body
      </Box>
    </Drawer>
  );
};

AddEventSidebar.propTypes = {
  addEventSidebarOpen: PropTypes.bool.isRequired,
  handleAddEventSidebarToggle: PropTypes.func.isRequired,
};

export { AddEventSidebar };
