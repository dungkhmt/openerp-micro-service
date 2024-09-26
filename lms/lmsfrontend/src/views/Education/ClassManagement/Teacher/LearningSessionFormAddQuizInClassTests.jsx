import React, { useState } from "react";
import {
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";
import { makeStyles } from "@material-ui/core/styles";
import { request } from "../../../../api";

const useStyles = makeStyles((theme) => ({
  formInput: {
    width: "100%",
    marginTop: "10px !important",
  },
}));

export default function LearningSessionFormAddQuizInClassTests(props) {
  const classes = useStyles();
  const { open, setOpen, sessionId, isCourse, onCreateSuccess } = props;
  const [testId, setTestId] = useState(null);
  const [testName, setTestName] = useState(null);
  const [description, setDescription] = useState("");
  const [numberOfQuizTests, setNumberOfQuizTests] = useState(1);

  function handleChangeTestId(e) {
    //setTestId(e.target.value);
    setNumberOfQuizTests(e.target.value);
  }
  function handleChangeTestName(e) {
    setTestName(e.target.value);
  }

  function perfromAddNewQuizTest() {
    let body = {
      sessionId: sessionId,
      //testId: testId,
      //testName: testName,
      interactiveQuizName: testName,
      status: "CREATED",
      statusId: "CREATED",
      description,
    };
    request(
      "POST",
      //"edu/class/add-a-quiz-test-of-class-session",
      isCourse
        ? "edu/course/create-course-session-interactive-quiz"
        : "create-interactive-quiz",
      (res) => {
        //alert("assign teacher to class " + res.data);
        //setIsProcessing(false);
        onCreateSuccess(res);
      },
      { 401: () => {} },
      body
    );
    setOpen(false);
  }
  return (
    <Dialog open={open}>
      <DialogTitle>Thêm quiz test</DialogTitle>
      <DialogContent>
        <div>
          <TextField
            label="TestName"
            onChange={handleChangeTestName}
            required
          ></TextField>
          <TextField
            label="Mô tả"
            multiline
            rows={4}
            maxRows={8}
            className={classes.formInput}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginTop: "20px",
          }}
        >
          <Button variant="outlined" onClick={perfromAddNewQuizTest}>
            Lưu
          </Button>
          <Button variant="outlined" onClick={() => setOpen(false)}>
            Đóng
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
