import React from "react";
import PropTypes from "prop-types";
import Button from "@material-ui/core/Button";
import {withStyles} from "@material-ui/core/styles";

const styles = {
  root: {
    borderRadius: "6px",
    backgroundColor: "#e4e6eb",
    textTransform: "none",
    fontSize: "1rem",
    fontWeight: 600,
    "&:hover": {
      backgroundColor: "#CCD0D5",
    },
  },
};

function NegativeButton(props) {
  const { label, className } = props;
  return (
    <Button className={className} {...props}>
      {label}
    </Button>
  );
}

NegativeButton.propTypes = {
  label: PropTypes.string.isRequired,
};

export default withStyles(styles)(NegativeButton);
