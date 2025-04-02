import PropTypes from "prop-types";
import { Button } from "@mui/material";
import { Icon } from "@iconify/react";

const RegisterButton = ({ hasData, onClick, disabled }) => {
  const getButtonStyles = () => {
    return hasData
      ? { bgcolor: "primary.main", hover: "primary.dark" }
      : { bgcolor: "warning.main", hover: "warning.dark" };
  };

  const { bgcolor, hover } = getButtonStyles();

  return (
    <Button
      variant="contained"
      startIcon={<Icon icon="mdi:calendar-check" />}
      fullWidth
      onClick={onClick}
      sx={{
        mt: 2,
        bgcolor,
        textTransform: "capitalize",
        borderRadius: 5,
        "&:hover": { bgcolor: hover },
      }}
      disabled={disabled}
    >
      {disabled ? "Đã đóng đăng ký" : "Đăng ký"}
    </Button>
  );
};

RegisterButton.propTypes = {
  hasData: PropTypes.bool.isRequired, // Changed to boolean
  onClick: PropTypes.func.isRequired,
  disabled: PropTypes.bool.isRequired,
};

export default RegisterButton;
