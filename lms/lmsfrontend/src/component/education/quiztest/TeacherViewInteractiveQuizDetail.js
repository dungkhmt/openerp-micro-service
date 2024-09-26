import {
  Box,
  Button,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Typography,
} from "@material-ui/core";
import { blue, grey, green } from "@material-ui/core/colors";
import { makeStyles, withStyles } from "@material-ui/core/styles";
import parse from "html-react-parser";
import React, { useEffect, useState } from "react";
import { FcDocument } from "react-icons/fc";
import SimpleBar from "simplebar-react";
import { request } from "../../../api";
import PrimaryButton from "../../button/PrimaryButton";
import TertiaryButton from "../../button/TertiaryButton";
import CustomizedDialogs from "../../dialog/CustomizedDialogs";
import ErrorDialog from "../../dialog/ErrorDialog";
import { Checkbox, FormControlLabel, FormGroup } from "@mui/material";
import { randomImageName } from "utils/FileUpload/covert";

function isPDF(base64Data) {
  const decodedData = atob(base64Data);
  return decodedData.startsWith("%PDF");
}

export const style = (theme) => ({
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
  list: {
    paddingBottom: 0,
    width: 330,
  },
  dialogContent: { paddingBottom: theme.spacing(1), width: 362 },
  listItem: {
    borderRadius: 6,
    "&:hover": {
      backgroundColor: grey[200],
    },
    "&.Mui-selected": {
      backgroundColor: blue[500],
      color: theme.palette.getContrastText(blue[500]),
      "&:hover": {
        backgroundColor: blue[500],
      },
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
});

const useStyles = makeStyles((theme) => style(theme));

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

export default function TeacherViewInteractiveQuizDetail({
  quiz,
  index,
  testId,
  isCourse,
  // quizGroups,
}) {
  const classes = useStyles();

  //
  // const [result, setResult] = useState({ submited: false, isCorrect: false });
  const [chkState, setChkState] = useState(() => {
    const isChecked = {};

    quiz.quizChoiceAnswerList.forEach((ans) => {
      isChecked[ans.choiceAnswerId] = false;
    });

    return isChecked;
  });

  // Modals.

  const [openQuizTest, setOpenQuizTest] = useState(null);
  const [selectedTestId, setSelectedTestId] = useState(null);
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
  const onOpenDialogQuizTest = () => {
    setOpenQuizTest(true);
  };
  const onSelectQuizTest = () => {
    handleCloseQuizTest();
    request(
      // token,
      // history,
      "post",
      isCourse
        ? "/add-question-to-course-interactive-quiz"
        : "/add-question-to-interactive-quiz",
      (res) => {},
      { rest: () => setError(true) },
      { interactiveQuizId: testId, questionId: quiz.questionId }
    );
  };

  const handleClose = () => {
    setOpen(false);
  };
  const handleCloseQuizTest = () => {
    setOpenQuizTest(false);
  };
  const handleChange = (event) => {
    setChkState({ ...chkState, [event.target.name]: event.target.checked });
  };
  return (
    <div className={classes.wrapper}>
      <Box className={classes.quizzStatement}>
        <Typography component="span">{`Câu ${index + 1}.`}&nbsp;</Typography>(
        {quiz.quizCourseTopic.quizCourseTopicName}:{quiz.levelId}:
        {quiz.statusId})&nbsp;&nbsp;
        {parse(quiz.statement)}
      </Box>
      {fileAttachments.length !== 0 &&
        fileAttachments.map((file) => (
          <div key={file.id} className={classes.imageContainer}>
            <div className={classes.imageWrapper}>
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

      <FormGroup row>
        {quiz.quizChoiceAnswerList.map((answer) => (
          <FormControlLabel
            key={answer.choiceAnswerId}
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
      <Button
        color="primary"
        variant="contained"
        onClick={onOpenDialogQuizTest}
        className={classes.btn}
      >
        Thêm vào kỳ thi
      </Button>

      {/* Dialogs */}
      <CustomizedDialogs
        open={openQuizTest}
        handleClose={handleCloseQuizTest}
        title="Thêm câu hỏi vào kỳ thi"
        content={<></>}
        actions={
          <>
            <TertiaryButton onClick={handleCloseQuizTest}>Huỷ</TertiaryButton>
            <PrimaryButton onClick={onSelectQuizTest}>
              Thêm vào kỳ thi
            </PrimaryButton>
          </>
        }
        style={{ content: classes.dialogContent }}
      />
    </div>
  );
}
