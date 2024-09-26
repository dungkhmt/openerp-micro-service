import DateFnsUtils from "@date-io/date-fns";
import {CircularProgress} from "@material-ui/core";
import {Card, CardActions, CardContent, TextField, Typography,} from "@material-ui/core/";
import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
import AlertDialog from "../../common/AlertDialog";

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

function CreateTopicOfCourse() {
  const params = useParams();
  const classes = useStyles();
  const courseId = params.courseId;
  const [quizCourseTopicId, setQuizCourseTopicId] = useState(null);
  const [quizCourseTopicName, setQuizCourseTopicName] = useState(null);
  const [isRequesting, setIsRequesting] = useState(false);
  const [alertMessage, setAlertMessage] = useState({
    title: "Vui lòng nhập đầy đủ thông tin cần thiết",
    content:
      "Một số thông tin yêu cầu cần phải được điền đầy đủ. Vui lòng kiểm tra lại.",
  });
  const [alertSeverity, setAlertSeverty] = useState("info");
  const [openAlert, setOpenAlert] = useState(false);
  const history = useHistory();

  const onClickAlertBtn = () => {
    setOpenAlert(false);
    if (reDirect != null) {
      history.push(reDirect);
    }
  };
  const handleCloseAlert = () => {
    setOpenAlert(false);
  };

  async function handleSubmit() {
    console.log("handle submit");
    let body = {
      quizCourseTopicId: courseId + "_" + quizCourseTopicId,
      quizCourseTopicName: quizCourseTopicName,
      courseId: courseId,
    };
    setIsRequesting(true);

    request(
      "post",
      "/create-quiz-course-topic",
      (res) => {
        setIsRequesting(false);
        if (res.data.message === "") {
          alert("Tạo thành công");
          console.log("Create topic success, topic = ", res);
          history.push("/edu/course/detail/" + courseId);
        } else {
          alert("Trùng ID");
          console.log("Create topic fail");
        }
      },
      {
        onError: (error) => {
          alert("Dữ liệu không đúng");
        },
      },
      body
    );
  }

  useEffect(() => {
    console.log("Create topic of course " + courseId);
  }, []);

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Tạo topic
          </Typography>
          <form className={classes.root} noValidate autoComplete="off">
            <div>
              <TextField
                required
                id="quizCourseTopicId"
                label="ID chủ đề"
                value={quizCourseTopicId}
                fullWidth
                onChange={(event) => {
                  setQuizCourseTopicId(event.target.value);
                  //console.log(problemId,event.target.value);
                }}
              ></TextField>
              <TextField
                required
                id="quizCourseTopicName"
                label="Tên chủ đề"
                value={quizCourseTopicName}
                fullWidth
                onChange={(event) => {
                  setQuizCourseTopicName(event.target.value);
                  //console.log(problemId,event.target.value);
                }}
              ></TextField>
            </div>
          </form>
        </CardContent>
        <CardActions>
          <Button
            disabled={isRequesting}
            variant="contained"
            color="primary"
            style={{ marginLeft: "45px" }}
            onClick={handleSubmit}
          >
            {isRequesting ? <CircularProgress /> : "Lưu"}
          </Button>
          <Button
            variant="contained"
            onClick={() => history.push("/edu/course/detail/" + courseId)}
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

export default CreateTopicOfCourse;
