import React, {useState} from "react";
import {Button, Dialog, DialogContent, DialogTitle, TextField} from "@material-ui/core";
import {request} from "../../../../api";

export default function LearningSessionFormAddQuizInClassTests(props) {
  const { open, setOpen, sessionId } = props;
  const [testId, setTestId] = useState(null);
  const [testName, setTestName] = useState(null);
  const [duration, setDuration] = useState(60);
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
      numberTests: numberOfQuizTests,
      duration: 60,
    };
    request(
      "POST",
      //"edu/class/add-a-quiz-test-of-class-session",
      "edu/class/add-some-quiz-tests-of-class-session",
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
      <DialogTitle>Thêm quiz test</DialogTitle>
      <DialogContent>
        <div
          style={{
            display: "flex",
            justifyContent: "space-around",
            minWidth: "500px",
            border: "1px solid black",
          }}
        >
          <TextField label="Number of Tests" onChange={handleChangeTestId}></TextField>
          <TextField
            label="TestName"
            onChange={handleChangeTestName}
          ></TextField>
        </div>
        <div>
          <Button onClick={perfromAddNewQuizTest}>Lưu</Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
