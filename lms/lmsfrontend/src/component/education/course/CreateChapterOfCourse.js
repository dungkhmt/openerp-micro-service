import DateFnsUtils from "@date-io/date-fns";
import {Card, CardActions, CardContent, TextField, Typography,} from "@material-ui/core/";
import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {EditorState} from "draft-js";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router";
import {useHistory} from "react-router-dom";
import {authPost} from "../../../api";
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

function CreateChapterOfCourse() {
  const params = useParams();
  const classes = useStyles();
  const courseId = params.courseId;
  const [chapterName, setChapterName] = useState(null);
  const [alertMessage, setAlertMessage] = useState({
    title: "Vui lòng nhập đầy đủ thông tin cần thiết",
    content:
      "Một số thông tin yêu cầu cần phải được điền đầy đủ. Vui lòng kiểm tra lại.",
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

  async function handleSubmit() {
    console.log("handle submit");
    let body = {
      courseId: courseId,
      chapterName: chapterName,
    };
    let chapter = await authPost(
      dispatch,
      token,
      "/edu/class/create-chapter-of-course",
      body
    );
    console.log("Create chapter success, chapter = ", chapter);
    history.push("/edu/course/detail/" + courseId);
  }
  useEffect(() => {
    console.log("Create chapter of course " + courseId);
  }, []);
  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Tạo bài tập
          </Typography>
          <form className={classes.root} noValidate autoComplete="off">
            <div>
              <TextField
                required
                id="chapterName"
                label="Tên chương"
                placeholder="Nhập tên chương"
                value={chapterName}
                onChange={(event) => {
                  setChapterName(event.target.value);
                }}
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
          <Button variant="contained" onClick={() => history.push("")}>
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

export default CreateChapterOfCourse;
