import React from "react";
import PropTypes from "prop-types";
import Button from "@material-ui/core/Button";
import {withStyles} from "@material-ui/core/styles";

const styles = {
  root: {
    borderRadius: "6px",
    backgroundColor: "#1877f2",
    textTransform: "none",
    fontSize: "1rem",
    fontWeight: 600,
    "&:hover": {
      backgroundColor: "#1834d2",
    },
  },
};

function PositiveButton(props) {
  const { label, className } = props;
  return (
    <Button
      variant="contained"
      color="primary"
      className={className}
      {...props}
    >
      {label}
    </Button>
  );
}

PositiveButton.propTypes = {
  label: PropTypes.string.isRequired,
};

export default withStyles(styles)(PositiveButton);
