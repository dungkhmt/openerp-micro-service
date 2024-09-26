import { Button, CircularProgress } from "@mui/material";
import { Box } from "@mui/system";
import PropTypes from "prop-types";

const LoadingButton = ({ loading, disabled = false, children, ...props }) => {
  return (
    <Button {...props} disabled={disabled || loading}>
      {children}
      {loading && (
        <Box
          sx={{
            position: "absolute",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            width: "100%",
            height: "100%",
            top: 0,
            left: 0,
          }}
        >
          <CircularProgress size={24} />
        </Box>
      )}
    </Button>
  );
};

LoadingButton.propTypes = {
  loading: PropTypes.bool,
  disabled: PropTypes.bool,
  children: PropTypes.node,
};

export { LoadingButton };
