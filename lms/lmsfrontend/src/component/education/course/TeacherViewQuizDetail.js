//import { Button, Box, Checkbox, Typography } from "@material-ui/core";

import {
  Box,
  Button,
  Typography,
  Checkbox,
  FormGroup,
  FormControlLabel,
} from "@mui/material";
import { grey, green } from "@material-ui/core/colors";
import { makeStyle, withStyles } from "@material-ui/core/styles";
import { makeStyles } from "@material-ui/core/styles";
import parse from "html-react-parser";
import { useEffect, useState } from "react";
import { useHistory } from "react-router";
import { request } from "../../../api";
import { randomImageName } from "../../../utils/FileUpload/covert";

function isPDF(base64Data) {
  const decodedData = atob(base64Data);
  // Check for PDF header
  return decodedData.startsWith("%PDF");
}

const useStyles = makeStyles(() => ({
  testBtn: {
    marginLeft: 40,
    marginTop: 32,
  },
  wrapper: {
    padding: "32px 0px",
  },
  answerWrapper: {
    "& label": {
      "&>:nth-child(2)": {
        display: "inline-block",
        "& p": {
          margin: 0,
          textAlign: "justify",
        },
      },
    },
  },
  answer: {
    width: "100%",
    marginTop: 20,
  },
  quizStatement: {
    fontSize: "1rem",
    "&>p:first-of-type": {
      display: "inline",
    },
  },
  bikeListDialog: {
    paddingBottom: 0,
    width: 330,
  },
  dialogContent: { paddingBottom: 0 },
  listItem: {
    borderRadius: 4,
    "&:hover": {
      backgroundColor: grey[200],
    },
  },
  btn: {
    textTransform: "none",
  },
  imageContainer: {
    marginTop: "12px",
  },
  imageWrapper: {
    position: "relative",
  },
  imageQuiz: {
    maxWidth: "100%",
  },
}));

/**
 * Customized checkbox.
 */
const GreenCheckbox = withStyles({
  root: {
    color: green[400],
    "&$checked": {
      color: green[600],
    },
    paddingLeft: 20,
    paddingTop: 0,
    paddingBottom: 0,
  },
  checked: {},
})((props) => <Checkbox color="default" disableRipple {...props} />);

/**
 * Describe a multiple-choice quiz.
 * @returns
 */
export default function TeacherViewQuizDetail({ quiz, index, courseId }) {
  const classes = useStyles();

  const history = useHistory();

  const [fileAttachments, setFileAttachments] = useState([]);

  useEffect(() => {
    if (quiz.attachment && quiz.attachment.length !== 0) {
      const newFileURLArray = quiz.attachment.map((url) => ({
        id: randomImageName(),
        url,
      }));
      setFileAttachments(newFileURLArray);
    }
  }, [quiz]);

  //
  const [result, setResult] = useState({ submited: false, isCorrect: false });
  const [chkState, setChkState] = useState(() => {
    const isChecked = {};

    quiz.quizChoiceAnswerList.forEach((ans) => {
      isChecked[ans.choiceAnswerId] = false;
    });

    return isChecked;
  });

  function handleUpdateQuizQuestion() {
    history.push(
      "/edu/teacher/course/quiz/detail/" + quiz.questionId + "/" + courseId
    );
  }

  function handleChangeStatus() {
    //alert('change status ' + quizz.questionId);
    request(
      // token,
      // history,
      "get",
      "change-quiz-open-close-status/" + quiz.questionId,
      (res) => {
        console.log(res);
        alert("change-quiz-open-close-status/ OK");
      },
      { 401: () => {} }
    );
  }

  const handleChange = (event) => {
    setChkState({ ...chkState, [event.target.name]: event.target.checked });
  };

  function handleViewDetailQuizQuestion() {
    history.push(
      "/edu/teacher/course/quiz/view/detail/" + quiz.questionId + "/" + courseId
    );
  }

  return (
    <div className={classes.wrapper}>
      <Box className={classes.quizzStatement}>
        <Typography component="span">{`Câu ${index + 1}.`}&nbsp;</Typography>(
        {quiz.quizCourseTopic.quizCourseTopicName}:{quiz.levelId}:
        {quiz.statusId})&nbsp;&nbsp; Created By: {quiz.createdByUserLoginId}
        {parse(quiz.statement)}
      </Box>
      <FormGroup row className={classes.answerWrapper}>
        {quiz.quizChoiceAnswerList.map((answer) => (
          <FormControlLabel
            key={answer.choiceAnswerId}
            className={classes.answer}
            control={
              <GreenCheckbox
                checked={chkState[answer.choiceAnswerId]}
                onChange={handleChange}
                name={answer.choiceAnswerId}
              />
            }
            label={parse(answer.choiceAnswerContent)}
          />
        ))}
      </FormGroup>
      {fileAttachments.length !== 0 &&
        fileAttachments.map((file) => (
          <div key={file.id} className={classes.imageContainer}>
            <div className={classes.imageWrapper}>
              {/* <img
                src={`data:image/jpeg;base64,${file.url}`}
                alt="quiz test"
                className={classes.imageQuiz}
              /> */}
              {isPDF(file.url) ? (
                <iframe
                  width={"860px"}
                  height={"500px"}
                  src={`data:application/pdf;base64,${file.url}`}
                />
              ) : (
                <img
                  src={`data:image/jpeg;base64,${file.url}`}
                  alt="quiz test"
                  className={classes.imageQuiz}
                />
              )}
            </div>
          </div>
        ))}
      <Button
        variant="contained"
        color="primary"
        onClick={() => handleChangeStatus()}
      >
        Thay đổi trạng thái
      </Button>
      &nbsp;&nbsp;
      <Button
        variant="contained"
        color="primary"
        onClick={() => handleUpdateQuizQuestion()}
      >
        Cập nhật
      </Button>
      &nbsp;&nbsp;
      <Button
        variant="contained"
        color="primary"
        onClick={() => handleViewDetailQuizQuestion()}
      >
        Xem chi tiết
      </Button>
    </div>
  );
}
