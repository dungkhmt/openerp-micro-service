import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";
import clsx from "clsx";
import React from "react";

const useStyles = makeStyles((theme) => ({
  button: {
    textTransform: "none",
  },
  buttonCorrect: {
    backgroundColor: "#33691e",
    "&:hover": {
      backgroundColor: "#33691e",
    },
  },
  buttonNotCorrect: {
    backgroundColor: "#d50000",
    "&:hover": {
      backgroundColor: "#d50000",
    },
  },
}));

export default function TestButton({ result, onClick, label }) {
  const classes = useStyles();

  const buttonClassname = clsx(classes.button, {
    [classes.buttonCorrect]: result.submited & result.isCorrect,
    [classes.buttonNotCorrect]: result.submited & !result.isCorrect,
  });

  return (
    <Button
      variant="contained"
      color="primary"
      className={buttonClassname}
      onClick={onClick}
    >
      {label}
    </Button>
  );
}
