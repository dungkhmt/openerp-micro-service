import {Button, Dialog, DialogContent, DialogTitle, TextField,} from "@material-ui/core";
import React, {useState} from "react";
import {request} from "../../../../api";

export default function TeacherCreateSessionForm(props) {
  const { open, setOpen, classId } = props;
  const [sessionName, setSessionName] = useState(null);
  const [description, setDescription] = useState(null);
  function handleChangeSessionName(e) {
    setSessionName(e.target.value);
  }
  function handleChangeSessionDescription(e) {
    setDescription(e.target.value);
  }
  function perfromAddNewSession() {
    let body = {
      classId: classId,
      sessionName: sessionName,
      description: description,
    };
    request(
      "POST",
      "edu/class/add-a-session-of-class",
      (res) => {
        //alert("assign teacher to class " + res.data);
        //setIsProcessing(false);
      },
      { 401: () => {} },
      body
    );
    setOpen(false);
  }
  return (
    <Dialog open={open}>
      <DialogTitle>Thêm buổi học</DialogTitle>
      <DialogContent>
        <div
          style={{
            display: "flex",
            justifyContent: "space-around",
            minWidth: "500px",
            border: "1px solid black",
          }}
        >
          <TextField
            label="Tên buổi học"
            onChange={handleChangeSessionName}
          ></TextField>
          <TextField
            label="Mô tả"
            onChange={handleChangeSessionDescription}
          ></TextField>
        </div>
        <div>
          <Button onClick={perfromAddNewSession}>Lưu</Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
