import {Box, Button, List, ListItem, ListItemIcon, ListItemText, Typography,} from "@material-ui/core";
import {blue, grey} from "@material-ui/core/colors";
import {makeStyles} from "@material-ui/core/styles";
import parse from "html-react-parser";
import React, {useState} from "react";
import {FcDocument} from "react-icons/fc";
import SimpleBar from "simplebar-react";
import {request} from "../../../api";
import PrimaryButton from "../../button/PrimaryButton";
import TertiaryButton from "../../button/TertiaryButton";
import CustomizedDialogs from "../../dialog/CustomizedDialogs";
import ErrorDialog from "../../dialog/ErrorDialog";

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

  const [openQuizTest, setOpenQuizTest] = useState(null);
  const [selectedTestId, setSelectedTestId] = useState(null);

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
      "/remove-question-from-quiz-test",
      (res) => {},
      { rest: () => setError(true) },
      { testId: testId, questionId: quiz.questionId }
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
      <Button
        color="primary"
        variant="contained"
        onClick={onOpenDialog}
        className={classes.btn}
      >
        Thêm vào đề
      </Button>

      <Button
        color="primary"
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
