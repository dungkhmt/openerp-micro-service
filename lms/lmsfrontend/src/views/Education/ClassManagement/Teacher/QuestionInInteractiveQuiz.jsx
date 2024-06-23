import { Box, Paper, TextField, Typography } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import { Skeleton } from "@material-ui/lab";
import { Fragment, useEffect, useState } from "react";
import { request } from "api";
import PrimaryButton from "component/button/PrimaryButton";
import TeacherViewQuizDetailInQuizTest from "component/education/quiztest/TeacherViewQuizDetailInQuizTest";
import {
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
} from "@mui/material";
import { errorNoti, successNoti } from "utils/notification";

const useStyles = makeStyles(() => ({
  titleContainer: {
    color: "white",
    backgroundColor: "#03787C",
    padding: 50,
  },
  title: { fontSize: "2.375rem" },
  body: {
    padding: "20px 30px",
  },
  quizzList: {
    padding: "0px 20px",
  },
  skeletonAnswer: {
    marginLeft: 20,
    marginTop: 20,
  },
}));

function QuestionInInteractiveQuiz({ testId, isCourse }) {
  const classes = useStyles();

  const [quizList, setQuizList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState("");
  //
  const getQuizListOfClass = () => {
    request(
      // token,
      // history,
      "get",
      isCourse
        ? `/get-questions-of-course-interactive-quiz/${testId}`
        : `/get-questions-of-interactive-quiz/${testId}`,
      (res) => {
        // console.log("getQuizListOfClass, res.data = ", res.data);
        setQuizList(res.data);
        setLoading(false);
      },
      {
        onError: () => {
          setLoading(false);
        },
      }
    );
  };

  const getInteractiveQuizStatus = () => {
    request(
      // token,
      // history,
      "get",
      `/get-status-of-interactive-quiz/${testId}`,
      (res) => {
        // console.log("getQuizListOfClass, res.data = ", res.data);
        setStatus(res.data);
      },
      {
        onError: () => {
          errorNoti("Cannot get interactive quiz Status!");
        },
      }
    );
  };

  const updateInteractiveQuizStatus = () => {
    request(
      // token,
      // history,
      "post",
      `/update-status-of-interactive-quiz/`,
      (res) => {
        successNoti("Update success");
      },
      {
        onError: () => {
          errorNoti("Cannot get interactive quiz Status!");
        },
      },
      {
        interactiveQuizId: testId,
        status: status,
      }
    );
  };

  function handleStatusChange(e) {
    setStatus(e.target.value);
  }

  useEffect(() => {
    getQuizListOfClass();
    if (!isCourse) getInteractiveQuizStatus();
  }, []);

  function updateQuestionListWhenRemoveSuccess(questionId) {
    setQuizList(quizList.filter((item) => item.questionId !== questionId));
  }

  return (
    <Paper elevation={8}>
      <div>
        <Box
          display="flex"
          alignItems="center"
          flexDirection="row"
          className={classes.titleContainer}
        >
          <Typography component="span" className={classes.title}>
            Câu hỏi trắc nghiệm
          </Typography>
        </Box>
      </div>
      <div className={classes.body}>
        <div className={classes.quizzList}>
          {!isCourse && (
            <div>
              <FormControl>
                <InputLabel htmlFor="status">Trạng thái</InputLabel>
                <Select
                  labelId="status"
                  value={status}
                  onChange={handleStatusChange}
                  displayEmpty
                  style={{ width: "150px", marginRight: "24px" }}
                  renderValue={() => status}
                  name="status"
                >
                  <MenuItem value={"OPENED"}>OPENED</MenuItem>
                  <MenuItem value={"HIDDEN"}>HIDDEN</MenuItem>
                  <MenuItem value={"CREATED"}>CREATED</MenuItem>
                </Select>
              </FormControl>
              <Button
                color="primary"
                variant="contained"
                onClick={updateInteractiveQuizStatus}
              >
                Update
              </Button>
            </div>
          )}
          {loading ? (
            <Fragment>
              <Skeleton
                variant="rect"
                width={800}
                height={24}
                animation="wave"
              />
              <Skeleton
                className={classes.skeletonAnswer}
                variant="rect"
                width={200}
                height={24}
                animation="wave"
              />
              <Skeleton
                className={classes.skeletonAnswer}
                variant="rect"
                width={600}
                height={24}
                animation="wave"
              />
              <Skeleton
                className={classes.skeletonAnswer}
                variant="rect"
                width={400}
                height={24}
                animation="wave"
              />
              <Skeleton
                className={classes.skeletonAnswer}
                variant="rect"
                width={700}
                height={24}
                animation="wave"
              />
            </Fragment>
          ) : (
            quizList.map((quiz, index) => (
              <TeacherViewQuizDetailInQuizTest
                isCourse={isCourse}
                key={quiz.questionId}
                quiz={quiz}
                index={index}
                testId={testId}
                // quizGroups={quizGroups}
                onRemoveSuccess={updateQuestionListWhenRemoveSuccess}
              />
            ))
          )}
        </div>
      </div>
    </Paper>
  );
}

export default QuestionInInteractiveQuiz;
