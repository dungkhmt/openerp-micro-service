import DateFnsUtils from "@date-io/date-fns";
import {Card, CardActions, CardContent, MenuItem, TextField, Typography,} from "@material-ui/core/";
import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {convertToRaw, EditorState} from "draft-js";
import draftToHtml from "draftjs-to-html";
import {DropzoneArea} from "material-ui-dropzone";
import {useEffect, useState} from "react";
import {Editor} from "react-draft-wysiwyg";
import {useParams} from "react-router";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
import AlertDialog from "../../common/AlertDialog";
import RichTextEditor from "../../common/editor/RichTextEditor";
import FileUploader from "../../common/uploader/FileUploader";
import withScreenSecurity from "../../withScreenSecurity";

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

function CreateQuizOfCourse() {
  const params = useParams();
  const classes = useStyles();
  const courseId = params.courseId;
  const [quizCourseTopicId, setQuizCourseTopicId] = useState(null);
  const [levelId, setLevelId] = useState(null);

  const [problemStatement, setProblemStatement] = useState(null);
  const [levelList, setLevelList] = useState([]);
  const [topicList, setTopicList] = useState([]);

  const [attachmentFiles, setAttachmentFiles] = useState([]);

  const [solutionContent, setSolutionContent] = useState("");
  const [solutionAttachments, setSolutionAttachments] = useState([]);

  const handleAttachmentFiles = (files) => {
    setAttachmentFiles(files);
  };

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

  const onChangeEditorState = (editorState) => {
    setEditorState(editorState);
  };

  async function getLevelList() {
    request("get", "/get-quiz-levels", (res) => {
      setLevelList(res.data);
    });
  }

  async function getTopicList() {
    request("get", "/get-quiz-course-topics-of-course/" + courseId, (res) => {
      setTopicList(res.data);
    });
  }

  async function handleSubmit() {
    let statement = draftToHtml(convertToRaw(editorState.getCurrentContent()));

    const fileId = attachmentFiles.map((file) => file.name);

    console.log("handle submit");
    let body = {
      quizCourseTopicId: quizCourseTopicId,
      levelId: levelId,
      questionContent: statement,
      fileId: fileId,
      solutionContent,
    };

    let formData = new FormData();
    formData.append("QuizQuestionCreateInputModel", JSON.stringify(body));
    for (const file of attachmentFiles) {
      formData.append("files", file);
    }

    for (const attachment of solutionAttachments) {
      formData.append("solutionAttachments", attachment);
    }

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/create-quiz-question",
      (res) => {
        history.push("/edu/course/detail/" + courseId);
      },
      {},
      formData,
      config
    );

    //let chapter = await authPost(dispatch, token, '/create-quiz-question', body);
    //console.log('Create chapter success, chapter = ',chapter);
  }
  useEffect(() => {
    getLevelList();
    getTopicList();
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
                id="levelId"
                select
                label="Mức độ"
                value={levelId}
                fullWidth
                onChange={(event) => {
                  setLevelId(event.target.value);
                  //console.log(problemId,event.target.value);
                }}
              >
                {levelList.map((item) => (
                  <MenuItem key={item} value={item}>
                    {item}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                required
                id="quizCourseTopicId"
                select
                label="Chủ đề"
                value={quizCourseTopicId}
                fullWidth
                onChange={(event) => {
                  setQuizCourseTopicId(event.target.value);
                  //console.log(problemId,event.target.value);
                }}
              >
                {topicList.map((item) => (
                  <MenuItem
                    key={item.quizCourseTopicId}
                    value={item.quizCourseTopicId}
                  >
                    {item.quizCourseTopicName}
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

            <Typography
              variant="subtitle1"
              display="block"
              style={{ margin: "5px 0 0 7px", width: "100%" }}
            >
              File đính kèm
            </Typography>
            <DropzoneArea
              dropzoneClass={classes.dropZone}
              filesLimit={20}
              showPreviews={true}
              showPreviewsInDropzone={false}
              useChipsForPreview
              dropzoneText="Kéo và thả tệp vào đây hoặc nhấn để chọn tệp"
              previewText="Xem trước:"
              previewChipProps={{
                variant: "outlined",
                color: "primary",
                size: "medium",
              }}
              getFileAddedMessage={(fileName) =>
                `Tệp ${fileName} tải lên thành công`
              }
              getFileRemovedMessage={(fileName) => `Tệp ${fileName} đã loại bỏ`}
              getFileLimitExceedMessage={(filesLimit) =>
                `Vượt quá số lượng tệp tối đa được cho phép. Chỉ được phép tải lên tối đa ${filesLimit} tệp.`
              }
              alertSnackbarProps={{
                anchorOrigin: { vertical: "bottom", horizontal: "right" },
                autoHideDuration: 1800,
              }}
              onChange={(files) => handleAttachmentFiles(files)}
            ></DropzoneArea>

            <div>
              <Typography variant="h6">Hướng dẫn làm bài</Typography>
              <RichTextEditor
                content={solutionContent}
                onContentChange={(solutionContent) =>
                  setSolutionContent(solutionContent)
                }
              />
              <FileUploader
                onChange={(files) => setSolutionAttachments(files)}
                multiple
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

const screenName = "SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER";
export default withScreenSecurity(CreateQuizOfCourse, screenName, true);
