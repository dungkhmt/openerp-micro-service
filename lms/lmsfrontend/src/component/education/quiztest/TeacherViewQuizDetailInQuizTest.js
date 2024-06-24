import {
  Box,
  Button,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Typography,
} from "@mui/material";
import { blue, grey } from "@material-ui/core/colors";
import { makeStyles } from "@material-ui/core/styles";
import parse from "html-react-parser";
import React, { useState, useEffect } from "react";
import { FcDocument } from "react-icons/fc";
import SimpleBar from "simplebar-react";
import { request } from "../../../api";
import PrimaryButton from "../../button/PrimaryButton";
import TertiaryButton from "../../button/TertiaryButton";
import CustomizedDialogs from "../../dialog/CustomizedDialogs";
import ErrorDialog from "../../dialog/ErrorDialog";
import { useHistory } from "react-router";
import { successNoti } from "utils/notification";
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
});

const useStyles = makeStyles((theme) => style(theme));

export default function TeacherViewQuizDetailInQuizTest({
  quiz,
  index,
  testId,
  quizGroups,
  isCourse,
  onRemoveSuccess,
}) {
  const classes = useStyles();

  //
  // const [result, setResult] = useState({ submited: false, isCorrect: false });
  // const [chkState, setChkState] = useState(() => {
  //   const isChecked = {};

  //   quiz.quizChoiceAnswerList.forEach((ans) => {
  //     isChecked[ans.choiceAnswerId] = false;
  //   });

  //   return isChecked;
  // });

  // Modals.
  const [open, setOpen] = useState(false);
  const [error, setError] = useState(false);
  const [selectedGroupId, setSelectedGroupId] = useState();
  const [fileAttachments, setFileAttachments] = useState([]);

  const [openQuizTest, setOpenQuizTest] = useState(null);
  const [selectedTestId, setSelectedTestId] = useState(null);
  const history = useHistory();
  const isQuizTest = history.location.pathname.includes("quiztest");

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
  const onOpenDialog = () => {
    setOpen(true);
  };
  const onOpenDialogQuizTest = () => {
    setOpenQuizTest(true);
  };

  const onSelectGroup = () => {
    handleClose();
    request(
      // token,
      // history,
      "post",
      "/add-quizgroup-question-assignment",
      (res) => {},
      { rest: () => setError(true) },
      { quizGroupId: selectedGroupId, questionId: quiz.questionId }
    );
  };
  const onRemoveFromQuizTest = () => {
    handleCloseQuizTest();
    request(
      // token,
      // history,
      "post",
      isQuizTest
        ? "/remove-question-from-quiz-test"
        : isCourse
        ? "/remove-question-from-course-interactive-quiz"
        : "/remove-question-from-interactive-quiz",
      (res) => {
        !isQuizTest && onRemoveSuccess(quiz.questionId);
        successNoti("Xóa thành công", 3000);
      },
      { rest: () => setError(true) },
      isQuizTest
        ? { testId: testId, questionId: quiz.questionId }
        : { interactiveQuizId: testId, questionId: quiz.questionId }
    );
  };

  const handleListItemClick = (event, groupId) => {
    setSelectedGroupId(groupId);
  };

  const handleClose = () => {
    setOpen(false);
  };
  const handleCloseQuizTest = () => {
    setOpenQuizTest(false);
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
      {isQuizTest && (
        <Button
          color="primary"
          variant="contained"
          onClick={onOpenDialog}
          className={classes.btn}
        >
          Thêm vào đề
        </Button>
      )}

      <Button
        color="error"
        variant="contained"
        onClick={onOpenDialogQuizTest}
        className={classes.btn}
      >
        Loại khỏi kỳ thi
      </Button>

      {/* Dialogs */}
      <CustomizedDialogs
        open={open}
        handleClose={handleClose}
        title="Thêm câu hỏi vào đề"
        content={
          <>
            <Typography color="textSecondary" gutterBottom>
              Chọn một đề trong danh sách dưới đây.
            </Typography>
            <SimpleBar
              style={{
                height: "100%",
                maxHeight: 400,
                width: 330,
                overflowX: "hidden",
                overscrollBehaviorY: "none", // To prevent tag <main> be scrolled when menu's scrollbar reach end
              }}
            >
              <List className={classes.list}>
                {quizGroups
                  ? quizGroups.map((group) => (
                      <ListItem
                        key={group.quizGroupId}
                        className={classes.listItem}
                        selected={selectedGroupId === group.quizGroupId}
                        onClick={(event) =>
                          handleListItemClick(event, group.quizGroupId)
                        }
                      >
                        <ListItemIcon>
                          <FcDocument size={24} />
                        </ListItemIcon>
                        <ListItemText primary={group.groupCode} />
                      </ListItem>
                    ))
                  : null}
              </List>
            </SimpleBar>
          </>
        }
        actions={
          <>
            <TertiaryButton onClick={handleClose}>Huỷ</TertiaryButton>
            <PrimaryButton onClick={onSelectGroup}>Thêm vào đề</PrimaryButton>
          </>
        }
        style={{ content: classes.dialogContent }}
      />
      <ErrorDialog open={error} />

      {/* Dialogs */}
      <CustomizedDialogs
        open={openQuizTest}
        handleClose={handleCloseQuizTest}
        title="Thêm câu hỏi vào kỳ thi"
        content={<></>}
        actions={
          <>
            <TertiaryButton onClick={handleCloseQuizTest}>Huỷ</TertiaryButton>
            <PrimaryButton onClick={onRemoveFromQuizTest}>
              Loại khỏi kỳ thi
            </PrimaryButton>
          </>
        }
        style={{ content: classes.dialogContent }}
      />
    </div>
  );
}
