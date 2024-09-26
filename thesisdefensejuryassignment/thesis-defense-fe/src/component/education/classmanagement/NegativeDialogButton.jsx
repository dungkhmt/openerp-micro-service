import React from "react";
import PropTypes from "prop-types";
import Button from "@material-ui/core/Button";
import {withStyles} from "@material-ui/core/styles";

const styles = {
  root: {
    borderRadius: "6px",
    textTransform: "none",
    fontSize: "1rem",
    "&:hover": {
      backgroundColor: "#e7f3ff",
    },
  },
};

function NegativeDialogButton(props) {
  const { label, className } = props;
  return (
    <Button color="primary" className={className} {...props}>
      {label}
    </Button>
  );
}

NegativeDialogButton.propTypes = {
  label: PropTypes.string.isRequired,
};

export default withStyles(styles)(NegativeDialogButton);
