import PropTypes from "prop-types";
import {
  Popover,
  MenuItem,
  Typography,
  Box,
  Divider,
  Button,
} from "@mui/material";
import { Icon } from "@iconify/react";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { ROLE_IDS } from "../../../constants/roles";

const OrganizationPopover = ({ anchorEl, onClose, onSelectOrganization }) => {
  const { currentOrganization, organizations } = useSelector(
    (state) => state.organization
  );

  const currentOrg = organizations.find(
    (org) => org.id === currentOrganization?.id
  );
  const otherOrgs = organizations.filter(
    (org) => org.id !== currentOrganization?.id
  );

  const buttonSx = {
    textTransform: "none",
    fontSize: "0.75rem",
    color: "text.secondary",
    border: "1px solid rgba(128, 128, 128, 0.2)",
    backgroundColor: "background.default",
    px: 2,
    py: 1,
    width: "auto",
    "&:hover": {
      color: "text.primary",
      backgroundColor: "background.default",
    },
  };

  return (
    <Popover
      open={Boolean(anchorEl)}
      anchorEl={anchorEl}
      onClose={onClose}
      anchorOrigin={{ vertical: "bottom", horizontal: "left" }}
      transformOrigin={{ vertical: "top", horizontal: "left" }}
      PaperProps={{
        sx: {
          mt: 1,
          borderRadius: 2,
          backgroundColor: "background.default",
          backdropFilter: "blur(10px)",
          boxShadow: "0 4px 12px rgba(0, 0, 0, 0.3)",
          minWidth: 280,
          maxWidth: 320,
          border: 1,
          borderColor: "grey.200",
          color: "grey.700",
          px: 3,
          py: 2,
        },
      }}
    >
      {organizations.length > 0 ? (
        <>
          {currentOrg && (
            <Box
              sx={{
                p: 2,
                pb: 3,
                mb: 1,
                position: "relative",
                backgroundColor: "action.selected",
                borderRadius: 2,
              }}
            >
              <Typography fontWeight={600} color="text.primary">
                {currentOrg.name}
              </Typography>
              <Typography
                variant="caption"
                color="text.primary"
                sx={{ textTransform: "capitalize" }}
              >
                {currentOrg.myRole || "Member"}
                <Icon
                  icon="material-symbols:circle"
                  width={6}
                  style={{ margin: "0 5px" }}
                />
                {currentOrg.memberCount || 0} Members
              </Typography>
              <Icon
                icon="material-symbols:check"
                style={{ position: "absolute", right: 16, top: 16 }}
                width={20}
                color="grey"
              />
              <Box sx={{ display: "flex", gap: 2, mt: 1.5 }}>
                <Button
                  size="small"
                  fullWidth
                  startIcon={<Icon icon="mdi:gear" />}
                  sx={buttonSx}
                >
                  Settings
                </Button>
                {currentOrg.myRole === ROLE_IDS.admin && (
                  <Button
                    size="small"
                    fullWidth
                    startIcon={<Icon icon="mdi:account-plus" />}
                    component={Link}
                    to="/settings/members"
                    onClick={onClose}
                    sx={buttonSx}
                  >
                    Invite members
                  </Button>
                )}
              </Box>
            </Box>
          )}

          {otherOrgs.length > 0 && (
            <Box>
              {otherOrgs.map((org) => (
                <MenuItem
                  key={org.id}
                  onClick={() => onSelectOrganization(org.id)}
                  sx={{
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "flex-start",
                    borderRadius: 2,
                    py: 1,
                    px: 2,
                    gap: 0.5,
                    "&:hover .hover-text": {
                      color: "text.primary",
                    },
                  }}
                >
                  <Typography
                    fontWeight={500}
                    noWrap
                    maxWidth="100%"
                    color="text.secondary"
                    className="hover-text"
                  >
                    {org.name}
                  </Typography>
                  <Typography
                    variant="caption"
                    color="text.secondary"
                    className="hover-text"
                    sx={{ textTransform: "capitalize" }}
                  >
                    {org.myRole || "Member"}
                    <Icon
                      icon="material-symbols:circle"
                      width={6}
                      style={{ margin: "0 5px" }}
                    />
                    {org.memberCount || 0} Members
                  </Typography>
                </MenuItem>
              ))}
            </Box>
          )}
        </>
      ) : (
        <Box sx={{ p: 2, textAlign: "center" }}>
          <Typography variant="body2" color="text.secondary">
            No organizations found.
          </Typography>
        </Box>
      )}

      <Divider sx={{ borderColor: "grey.300", my: 1 }} />

      {[
        {
          icon: "mdi:plus",
          label: "Create organization",
          to: "/new-organization",
        },
        {
          icon: "mdi:email-outline",
          label: "Organization invites",
          to: "/invitations",
        },
      ].map(({ icon, label, to }) => (
        <MenuItem key={label} sx={{ py: 1, px: 2, borderRadius: 2 }}>
          <Icon icon={icon} width={18} style={{ marginRight: 8 }} />
          <Typography
            variant="body2"
            color="text.primary"
            component={to ? Link : "span"}
            to={to}
            onClick={onClose}
            sx={{ textDecoration: "none", cursor: "pointer" }}
          >
            {label}
          </Typography>
        </MenuItem>
      ))}
    </Popover>
  );
};

OrganizationPopover.propTypes = {
  anchorEl: PropTypes.any.isRequired,
  onClose: PropTypes.func.isRequired,
  onSelectOrganization: PropTypes.func.isRequired,
};

export default OrganizationPopover;
