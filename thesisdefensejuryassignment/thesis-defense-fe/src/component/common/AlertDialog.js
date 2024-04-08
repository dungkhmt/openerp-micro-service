import React from "react";
import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle,} from "@material-ui/core/";
import {COLOR} from "./Config";

const setColor = {
  success: COLOR.green,
  info: COLOR.blue,
  warning: COLOR.orange,
  error: COLOR.red,
};

export default function AlertDialog(props) {
  const { open, onClose, title, content, buttons, severity } = props;

  const colors = setColor;

  return (
    <Dialog
      open={open}
      onClose={onClose}
      aria-labelledby="alert-dialog-title"
      aria-describedby="alert-dialog-description"
    >
      <DialogTitle id="alert-dialog-title">
        <span style={{ color: colors[severity] || "black" }}>{title}</span>
      </DialogTitle>
      <DialogContent>
        <DialogContentText id="alert-dialog-description">
          <span style={{ color: colors[severity] || "black" }}>{content}</span>
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        {buttons.map((btn, index) => {
          return (
            <Button {...btn} key={index}>
              {btn.text}
            </Button>
          );
        })}
      </DialogActions>
    </Dialog>
  );
}
