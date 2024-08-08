import DateFnsUtils from "@date-io/date-fns";
import {
  Card,
  CardActions,
  CardContent,
  MenuItem,
  TextField,
  Typography,
} from "@mui/material";
import Button from "@mui/material/Button";
import { makeStyles } from "@material-ui/core/styles";
import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import { convertToRaw, EditorState } from "draft-js";
import draftToHtml from "draftjs-to-html";
import { useEffect, useState } from "react";
import { Editor } from "react-draft-wysiwyg";
import { useParams } from "react-router";
import { useHistory } from "react-router-dom";
import { request } from "../../../api";
import AlertDialog from "../../common/AlertDialog";

let reDirect = null;
const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      marginBottom: theme.spacing(1),
      // width: "100px",
      width: 120,
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

function CreateQuizChoiceAnswerOfCourse() {
  const params = useParams();
  const classes = useStyles();
  const courseId = params.courseId;
  const questionId = params.questionId;
  const [choiceAnswerContent, setChoiceAnswerContent] = useState(null);
  const [isCorrectAnswer, setIsCorrectAnswer] = useState(null);
  const [yesno, setYesno] = useState([]);

  const [alertMessage, setAlertMessage] = useState({
    title: "Vui lòng nhập đầy đủ thông tin cần thiết",
    content:
      "Một số thông tin yêu cầu cần phải được điền đầy đủ. Vui lòng kiểm tra lại.",
  });
  const [alertSeverity, setAlertSeverty] = useState("info");
  const [openAlert, setOpenAlert] = useState(false);
  const history = useHistory();

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
    request("get", "/get-yes-no-list", (res) => {
      setYesno(res.data);
    });
  }

  const onChangeEditorState = (editorState) => {
    setEditorState(editorState);
  };

  async function handleSubmit() {
    let statement = draftToHtml(convertToRaw(editorState.getCurrentContent()));

    console.log("handle submit");
    let body = {
      choiceAnswerContent: statement,
      isCorrectAnswer: isCorrectAnswer,
      quizQuestionId: questionId,
    };

    request(
      "post",
      "/create-quiz-choice-answer",
      (res) => {
        //history.push("/edu/teacher/course/quiz/detail/" + questionId);
        history.push(
          "/edu/teacher/course/quiz/detail/" + questionId + "/" + courseId
        );
        //course/quiz/detail/264e1fec-1f92-4687-ad9b-6396a3d9ecc9/IT3170
      },
      {},
      body
    );
  }

  useEffect(() => {
    getYesNoList(); // need to be upgraded
  }, []);

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Tạo đáp án
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
            color="success"
            style={{ marginLeft: "45px" }}
            onClick={handleSubmit}
          >
            Lưu
          </Button>
          <Button
            variant="contained"
            color="error"
            onClick={() => history.push("")}
          >
            Hủy
          </Button>
        </CardActions>
      </Card>

      <AlertDialog
        open={openAlert}
        onClose={handleCloseAlert}
        severity={alertSeverity}
        {...alertMessage}
        buttons={[
          {
            onClick: onClickAlertBtn,
            color: "primary",
            autoFocus: true,
            text: "OK",
          },
        ]}
      />
    </MuiPickersUtilsProvider>
  );
}

export default CreateQuizChoiceAnswerOfCourse;
