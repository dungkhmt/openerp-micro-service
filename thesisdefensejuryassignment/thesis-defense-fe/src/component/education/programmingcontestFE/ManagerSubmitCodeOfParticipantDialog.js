import React from "react";
import {Button, Dialog, DialogContent, DialogTitle} from "@mui/material";
import ManagerSubmitCodeOfParticipant from "./ManagerSubmitCodeOfParticipant";

export default function ManagerSubmitCodeOfParticipantDialog(props) {
  const { contestId, onClose, open } = props;
  function handleClick() {
    onClose();
  }
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>Submissions</DialogTitle>
      <DialogContent>
        <ManagerSubmitCodeOfParticipant
          contestId={contestId}
          onClose={onClose}
        />
        <Button onClick={handleClick}>Close</Button>
      </DialogContent>
    </Dialog>
  );
}
