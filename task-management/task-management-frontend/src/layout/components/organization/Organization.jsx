import { Box, Typography, CircularProgress, IconButton } from "@mui/material";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { Icon } from "@iconify/react";
import { switchOrganization } from "../../../store/organization";
import OrganizationPopover from "./OrganizationPopover";

const Organization = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { currentOrganization, organizations, fetchLoading } = useSelector(
    (state) => state.organization
  );
  const [anchorEl, setAnchorEl] = useState(null);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleSwitchOrganization = async (orgId) => {
    try {
      await dispatch(switchOrganization(orgId)).unwrap();
      navigate(`/dashboard`);
      handleClose();
    } catch (error) {
      console.error("Failed to switch organization:", error);
    }
  };

  if (fetchLoading)
    return <CircularProgress size={24} sx={{ color: "grey.50" }} />;

  return (
    <Box sx={{ position: "relative", display: "inline-block" }}>
      <IconButton
        onClick={handleClick}
        sx={{
          color: "grey.50",
          display: "flex",
          alignItems: "center",
          borderRadius: 1,
          px: 3,
          py: 1,
          gap: 1,
          ml: -2,
          maxWidth: 200,
          backgroundColor: "transparent",
          transition: "background-color 0.3s ease, backdrop-filter 0.3s ease",
          "&:hover": {
            backgroundColor: "customColors.darkBg",
            backdropFilter: "blur(10px)",
          },
        }}
      >
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            width: "100%",
            justifyContent: "space-between",
            gap: 1,
            overflow: "hidden",
          }}
        >
          <Typography
            sx={{
              fontSize: "1rem",
              fontWeight: 500,
              lineHeight: "24px",
              color: "grey.50",
              whiteSpace: "nowrap", 
              overflow: "hidden", 
              textOverflow: "ellipsis", 
              flex: 1, 
            }}
          >
            {currentOrganization?.name || "Select Organization"}
          </Typography>

          <Box
            sx={{
              visibility: "hidden",
              opacity: 0,
              transition: "opacity 0.2s ease",
              "&:hover": {
                visibility: "visible",
                opacity: 1,
              },
              ".MuiIconButton-root:hover &": {
                visibility: "visible",
                opacity: 1,
              },
            }}
          >
            <Icon icon="mingcute:down-fill" width={16} height={16} />
          </Box>
        </Box>
      </IconButton>

      <OrganizationPopover
        anchorEl={anchorEl}
        onClose={handleClose}
        organizations={organizations}
        currentOrganization={currentOrganization}
        onSelectOrganization={handleSwitchOrganization}
      />
    </Box>
  );
};

export default Organization;
