import React, {useState} from "react";
import {useHistory, useParams} from "react-router-dom";
import {Button, Grid, TextField} from "@mui/material";
import {request} from "../../../api";
import QuizViewForCheckCode from "./QuizViewForCheckCode";
import { LoadingButton } from "@mui/lab";

export default function StudentQuizDetailCheckAndConfirmGroupCode() {
  const { testId } = useParams();
  const history = useHistory();
  const [groupCode, setGroupCode] = useState("");
  const checkState = useState([]);
  const [questions, setQuestions] = useState([]);
  const [quizGroupTestDetail, setQuizGroupTestDetail] = useState({});
  const [messageRequest, setMessageRequest] = useState(false);
  const [requestFailed, setRequestFailed] = useState(false);
  const [loading, setLoading] = useState(false);
  function onConfirmCode() {
    setLoading(true);
    request(
      "get",
      "/confirm-update-group-code-quiz-test/" + testId + "/" + groupCode,
      (res) => {
        alert("update " + res.data);
        setLoading(false);
        history.push("/edu/class/student/quiztest/detail/" + testId);
        //setOpen(false);
      },
      {
        401: () => {},
        406: () => {
          //setMessageRequest("Time Out!");
          setRequestFailed(true);
        },
      }
    );
  }

  function onCheckCode() {
    request(
      "get",
      "/check-questions-of-group/" + testId + "/" + groupCode,
      (res) => {
        const {
          listQuestion,
          participationExecutionChoice,
          ...quizGroupTestDetail
        } = res.data;

        setQuestions(listQuestion);
        setQuizGroupTestDetail(quizGroupTestDetail);

        //setViewTypeId(quizGroupTestDetail.viewTypeId);

        // Restore test result
        // TODO: optimize code
        const chkState = [];

        listQuestion.forEach((question) => {
          const choices = {};
          const choseAnswers =
            participationExecutionChoice[question.questionId];

          question.quizChoiceAnswerList.forEach((ans) => {
            choices[ans.choiceAnswerId] = false;
          });

          choices.submitted = false;
          if (choseAnswers) {
            choseAnswers.forEach((choseAnsId) => {
              choices[choseAnsId] = true;
            });

            choices.submitted = true;
            choices["lastSubmittedAnswers"] = choseAnswers;
          } else {
            choices["lastSubmittedAnswers"] = [];
          }

          chkState.push(choices);
        });

        checkState.set(chkState);
      },
      {
        401: () => {},
        406: () => {
          setMessageRequest("Time Out!");
          //setRequestFailed(true);
        },
      }
    );
  }

  return (
    <div>
      <div>
        <TextField
          autoFocus
          id="Code"
          label="Code"
          placeholder="Code"
          onChange={(event) => {
            setGroupCode(event.target.value);
          }}
          value={groupCode}
        />
        {/*
        <Button onClick={onCheckCode}>Check Out</Button>
         */}
      </div>
      <div>
        <LoadingButton
          //variant="contained"
          //color="light"
          //style={{ marginLeft: "45px" }}
          variant="contained"
                  color="primary"
          onClick={onConfirmCode}
          loading={loading}
                  style={{
                    textTransform: "none",
                    width: 100,
                  }}
        >
          CONFIRM
        </LoadingButton>
      </div>
      <Grid container spacing={3}>
        {quizGroupTestDetail.quizGroupId ? (
          questions != null ? (
            questions.map((question, idx) => (
              <QuizViewForCheckCode
                key={question.questionId}
                question={question}
                choseAnswers={checkState[idx]}
                order={idx}
              />
            ))
          ) : (
            <p style={{ justifyContent: "center" }}> Questions not available</p>
          )
        ) : (
          <p style={{ justifyContent: "center" }}> </p>
        )}
      </Grid>
    </div>
  );
}
