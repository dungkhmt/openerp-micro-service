import { Button, Tooltip, Typography, Box } from "@mui/material";
import { Icon } from "@iconify/react";
import PropTypes from "prop-types";

const StatusActionButton = ({
  label,
  icon,
  instruction,
  tooltip,
  onClick,
  disabled = false,
  color = "primary",
  isCancel = false,
}) => {
  return (
    <Box
      sx={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        gap: 1.5,
      }}
    >
      <Typography
        variant="body2"
        color="text.primary"
        sx={{ fontWeight: 500, textAlign: "center" }}
      >
        {instruction}
      </Typography>
      <Tooltip title={tooltip || instruction}>
        <Button
          variant={isCancel ? "outlined" : "contained"}
          startIcon={<Icon icon={icon} fontSize={18} />}
          onClick={onClick}
          disabled={disabled}
          sx={{
            borderRadius: 2,
            textTransform: "none",
            px: isCancel ? 3 : 4,
            py: isCancel ? 1 : 1.5,
            fontSize: "0.875rem",
            fontWeight: 600,
            color: "grey.100",
            bgcolor: color,
            "&:hover": {
              bgcolor: color,
            },
          }}
        >
          {label}
        </Button>
      </Tooltip>
    </Box>
  );
};

StatusActionButton.propTypes = {
  label: PropTypes.string,
  icon: PropTypes.string,
  instruction: PropTypes.string,
  tooltip: PropTypes.string,
  onClick: PropTypes.func,
  disabled: PropTypes.bool,
  color: PropTypes.string,
  isCancel: PropTypes.bool,
};

export default StatusActionButton;
