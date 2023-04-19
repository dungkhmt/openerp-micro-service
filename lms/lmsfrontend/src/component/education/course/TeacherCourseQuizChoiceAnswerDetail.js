import DateFnsUtils from "@date-io/date-fns";
import Button from "@material-ui/core/Button";
import {Card, CardActions, CardContent, MenuItem, TextField, Typography,} from "@material-ui/core/";
import {makeStyles} from "@material-ui/core/styles";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import React, {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {authDelete, authGet, authPost} from "../../../api";
import {useDispatch, useSelector} from "react-redux";
import AlertDialog from "../../common/AlertDialog";
import {Editor} from "react-draft-wysiwyg";
import {useParams} from "react-router";
import {ContentState, convertToRaw, EditorState} from "draft-js";

import draftToHtml from "draftjs-to-html";
import htmlToDraft from "html-to-draftjs";

let reDirect = null;
const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: "100%",
      minWidth: 120,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
}));

const editorStyle = {
  toolbar: {
    background: "#90caf9",
  },
  editor: {
    border: "1px solid black",
    minHeight: "300px",
  },
};

function TeacherCourseQuizChoiceAnswerDetail() {
  const params = useParams();
  const classes = useStyles();
  const choiceAnswerId = params.choiceAnswerId;
  const [questionId, setQuestionId] = useState(null);
  const [courseId, setCourseId] = useState(null);
  const [choiceAnswerContent, setChoiceAnswerContent] = useState(null);
  const [isCorrectAnswer, setIsCorrectAnswer] = useState(null);
  const [yesno, setYesno] = useState([]);
  const [initState, setInitState] = useState(false);
  const [alertMessage, setAlertMessage] = useState({
    title: "Vui lòng kiểm tra lại",
    content: "Bạn có chắc chắn muốn xóa?",
  });
  const [alertSeverity, setAlertSeverty] = useState("info");
  const [openAlert, setOpenAlert] = useState(false);
  const history = useHistory();
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const [editorState, setEditorState] = useState(EditorState.createEmpty());

  const onClickAlertBtn = () => {
    setOpenAlert(false);
    if (reDirect != null) {
      history.push(reDirect);
    }
  };
  const handleCloseAlert = () => {
    setOpenAlert(false);
  };
  async function getYesNoList() {
    let lst = await authGet(dispatch, token, "/get-yes-no-list");
    setYesno(lst);
  }
  const onChangeEditorState = (editorState) => {
    setEditorState(editorState);
  };

  async function getQuizChoiceAnswerDetail() {
    await authGet(
      dispatch,
      token,
      "/get-quiz-choice-answer-detail/" + choiceAnswerId
    ).then(
      (res) => {
        if (res) {
          setIsCorrectAnswer(res.isCorrectAnswer);
          let blocksFromHtml = htmlToDraft(res.choiceAnswerContent);
          let { contentBlocks, entityMap } = blocksFromHtml;
          let contentState = ContentState.createFromBlockArray(
            contentBlocks,
            entityMap
          );
          let statement = EditorState.createWithContent(contentState);
          setEditorState(statement);
          setInitState(true);
          setQuestionId(res.quizQuestion.questionId);
          setCourseId(res.quizQuestion.quizCourseTopic.eduCourse.id);
          //alert(JSON.stringify(res));
        } else {
          alert("Lỗi kết nối, thử tải lại trang");
        }
      },
      (error) => {
        alert("Lỗi kết nối, thử tải lại trang");
      }
    );
  }

  async function handleSubmit() {
    let statement = draftToHtml(convertToRaw(editorState.getCurrentContent()));

    console.log("handle submit");
    let body = {
      choiceAnswerContent: statement,
      isCorrectAnswer: isCorrectAnswer,
      quizQuestionId: questionId,
    };
    await authPost(
      dispatch,
      token,
      "/update-quiz-choice-answer/" + choiceAnswerId,
      body
    ).then(
      (res) => {
        if (res.length !== 0) {
          alert("Cập nhật thành công");
        } else {
          alert("Cập nhật không thành công");
        }
        history.push(
          "/edu/teacher/course/quiz/detail/" + questionId + "/" + courseId
        );
      },
      (error) => {
        alert("Cập nhật không thành công");
        //history.push("/edu/teacher/course/quiz/detail/" + questionId);
        history.push(
          "/edu/teacher/course/quiz/detail/" + questionId + "/" + courseId
        );
      }
    );
  }

  async function handleDelete() {
    await authDelete(
      dispatch,
      token,
      "/delete-quiz-choice-answer/" + choiceAnswerId
    ).then(
      (res) => {
        if (res.length !== 0) {
          alert("Xóa thành công");
        } else {
          alert("Xóa không thành công");
        }
        //history.push("/edu/teacher/course/quiz/detail/" + questionId);
        history.push(
          "/edu/teacher/course/quiz/detail/" + questionId + "/" + courseId
        );
      },
      (error) => {
        alert("Xóa không thành công");
        //history.push("/edu/teacher/course/quiz/detail/" + questionId);
        history.push(
          "/edu/teacher/course/quiz/detail/" + questionId + "/" + courseId
        );
      }
    );
  }
  useEffect(() => {
    getQuizChoiceAnswerDetail();
    getYesNoList(); // need to be upgraded
  }, []);
  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      {!initState ? (
        <div />
      ) : (
        <Card>
          <CardContent>
            <Typography variant="h5" component="h2">
              Thông tin câu trả lời
            </Typography>
            <form className={classes.root} noValidate autoComplete="off">
              <div>
                <TextField
                  required
                  id="isCorrectAnswer"
                  select
                  label="Đáp án"
                  value={isCorrectAnswer}
                  fullWidth
                  onChange={(event) => {
                    setIsCorrectAnswer(event.target.value);
                    //console.log(problemId,event.target.value);
                  }}
                >
                  {yesno.map((item) => (
                    <MenuItem key={item} value={item}>
                      {item}
                    </MenuItem>
                  ))}
                </TextField>

                <Editor
                  editorState={editorState}
                  handlePastedText={() => false}
                  onEditorStateChange={onChangeEditorState}
                  toolbarStyle={editorStyle.toolbar}
                  editorStyle={editorStyle.editor}
                />
              </div>
            </form>
          </CardContent>
          <CardActions>
            <Button
              variant="contained"
              color="primary"
              style={{ marginLeft: "45px" }}
              onClick={handleSubmit}
            >
              Lưu
            </Button>

            <Button
              variant="contained"
              color="secondary"
              onClick={() => {
                setOpenAlert(true);
              }}
            >
              Xóa
            </Button>

            <Button
              variant="contained"
              onClick={() =>
                history.push("/edu/teacher/course/quiz/detail/" + questionId)
              }
            >
              Hủy
            </Button>
          </CardActions>
        </Card>
      )}

      <AlertDialog
        open={openAlert}
        onClose={handleCloseAlert}
        severity={alertSeverity}
        {...alertMessage}
        buttons={[
          {
            onClick: handleDelete,
            color: "primary",
            autoFocus: true,
            text: "OK",
          },
          {
            onClick: handleCloseAlert,
            color: "primary",
            autoFocus: true,
            text: "Hủy",
          },
        ]}
      />
    </MuiPickersUtilsProvider>
  );
}

export default TeacherCourseQuizChoiceAnswerDetail;
