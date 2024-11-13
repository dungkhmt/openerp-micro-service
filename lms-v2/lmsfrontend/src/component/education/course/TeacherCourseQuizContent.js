import DateFnsUtils from "@date-io/date-fns";
import {
  Card,
  CardActions,
  CardContent,
  MenuItem,
  TextField,
  Typography,
  Divider,
} from "@mui/material";
import Button from "@mui/material/Button";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import { ContentState, convertToRaw, EditorState } from "draft-js";
import draftToHtml from "draftjs-to-html";
import htmlToDraft from "html-to-draftjs";
import { DropzoneArea } from "material-ui-dropzone";
import { useEffect, useState } from "react";
import { Editor } from "react-draft-wysiwyg";
import { useParams } from "react-router";
import { useHistory } from "react-router-dom";
import { request } from "../../../api";
import {
  dataUrlToFile,
  randomImageName,
} from "../../../utils/FileUpload/covert";
import AlertDialog from "../../common/AlertDialog";
import RichTextEditor from "../../common/editor/RichTextEditor";
import FilePreview from "../../common/uploader/FilePreview";
import FileUploader from "../../common/uploader/FileUploader";
import getFileByStorageId from "../quiztest/quizdoingexplanation/content-utils";
import { Box, Chip, InputLabel, OutlinedInput, Select } from "@mui/material";
import { errorNoti } from "utils/notification";

let reDirect = null;
const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      marginTop: theme.spacing(1),
      width: "51%",
      minWidth: 120,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
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
  buttonClearImage: {
    position: "absolute",
    top: "12px",
    right: "12px",
    zIndex: 3,
    color: "red",
    width: 32,
    height: 32,
    cursor: "pointer",
  },
  selectBox: {
    padding: 20,
    minWidth: 150,
    width: "51%",
    marginBottom: 20,
    height: 60,
  },
  wrapper: {
    padding: 32,
  },
  subTitle: {
    fontSize: 20,
    fontWeight: 600,
    marginBottom: 16,
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

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

function getStyles(tag, tags, theme) {
  return {
    fontWeight:
      tags.indexOf(tag) === -1
        ? theme.typography.fontWeightRegular
        : theme.typography.fontWeightMedium,
  };
}

function CreateQuizOfCourse() {
  const params = useParams();
  const classes = useStyles();
  const questionId = params.questionId;
  const history = useHistory();

  const [quizCourseTopicId, setQuizCourseTopicId] = useState(null);
  const [levelId, setLevelId] = useState(null);
  const [courseId, setCourseId] = useState(null);
  const [problemStatement, setProblemStatement] = useState(null);
  const [levelList, setLevelList] = useState([]);
  const [topicList, setTopicList] = useState([]);

  const [attachmentFiles, setAttachmentFiles] = useState([]);
  const [fetchedImageArray, setFetchedImageArray] = useState([]);

  const [initState, setInitSate] = useState(false);

  const [solutionContent, setSolutionContent] = useState("");
  const [solutionAttachmentIds, setSolutionAttachmentIds] = useState([]);
  const [solutionAttachments, setSolutionAttachments] = useState([]);
  const [deletedAttachmentIds, setDeletedAttachmentIds] = useState([]);
  const [addedSolutionAttachments, setAddedSolutionAttachments] = useState([]);

  const theme = useTheme();
  const [tags, setTags] = useState([]);
  const [chooseTags, setChooseTags] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(getSolutionAttachments, [solutionAttachmentIds]);

  async function getSolutionAttachments() {
    console.log("solutionAttachmentIds", solutionAttachmentIds);
    let attachments = await Promise.all(
      solutionAttachmentIds.map((attachmentId) =>
        getFileByStorageId(attachmentId)
      )
    );
    setSolutionAttachments(attachments);
  }

  function removeOldSolutionAttachments(attachmentIndex) {
    setDeletedAttachmentIds([
      ...deletedAttachmentIds,
      solutionAttachmentIds[attachmentIndex],
    ]);
    solutionAttachments.splice(attachmentIndex, 1);
    setSolutionAttachments([...solutionAttachments]);
  }

  const [alertMessage, setAlertMessage] = useState({
    title: "Vui lòng nhập đầy đủ thông tin cần thiết",
    content:
      "Một số thông tin yêu cầu cần phải được điền đầy đủ. Vui lòng kiểm tra lại.",
  });
  const [alertSeverity, setAlertSeverty] = useState("info");
  const [openAlert, setOpenAlert] = useState(false);

  const [editorState, setEditorState] = useState(EditorState.createEmpty());

  const handleAttachmentFiles = (files) => {
    setAttachmentFiles(files);
    //alert(JSON.stringify(files));
  };

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

  async function getTopicList(courseId) {
    request("get", "/get-quiz-course-topics-of-course/" + courseId, (res) => {
      setTopicList(res.data);
    });
  }

  async function getListTagOfCourse(courseId) {
    request("GET", `/get-tags-of-course/${courseId}`, (res) =>
      setTags(res.data.map((item) => item.tagName))
    );
  }

  async function getQuizContent() {
    request("get", "/edu/teacher/course/quiz/detail/" + questionId, (res) => {
      res = res.data;

      if (res) {
        if (res.attachment && res.attachment.length !== 0) {
          const newFileURLArray = res.attachment.map((url) => ({
            id: randomImageName(),
            url,
          }));
          setFetchedImageArray(newFileURLArray);
        }
        let quizQuestion = res;
        setQuizCourseTopicId(quizQuestion.quizCourseTopic.quizCourseTopicId);
        setLevelId(quizQuestion.levelId);
        setSolutionContent(quizQuestion.solutionContent ?? "");
        setSolutionAttachmentIds(quizQuestion.solutionAttachmentIds);
        let blocksFromHtml = htmlToDraft(quizQuestion.questionContent);
        let { contentBlocks, entityMap } = blocksFromHtml;
        let contentState = ContentState.createFromBlockArray(
          contentBlocks,
          entityMap
        );
        let statement = EditorState.createWithContent(contentState);
        setEditorState(statement);
        setCourseId(quizQuestion.quizCourseTopic.eduCourse.id);

        const courseId = quizQuestion.quizCourseTopic.eduCourse.id;
        getListTagOfCourse(courseId);
        getTopicList(courseId);
        getLevelList();
        setInitSate(true);
      } else {
        alert("Lỗi kết nối, thử tải lại trang");
      }
    });
  }

  async function handleSubmit() {
    let statement = draftToHtml(convertToRaw(editorState.getCurrentContent()));

    const fetchedFileArray = [];
    for (const fetchedFile of fetchedImageArray) {
      const file = await dataUrlToFile(
        `data:image/jpeg;base64,${fetchedFile.url}`,
        fetchedFile.id
      );
      fetchedFileArray.push(file);
    }

    const newAttachmentFiles = [...fetchedFileArray, ...attachmentFiles];

    const fileId = newAttachmentFiles.map((file) => {
      if (typeof file.name !== "undefined") {
        return file.name;
      }
      return file.id;
    });

    console.log("handle submit");
    let body = {
      quizCourseTopicId: quizCourseTopicId,
      levelId: levelId,
      questionContent: statement,
      fileId,
      solutionContent,
      deletedAttachmentIds,
      chooseTags,
      courseId,
    };

    let formData = new FormData();
    formData.append("QuizQuestionUpdateInputModel", JSON.stringify(body));
    for (const file of newAttachmentFiles) {
      formData.append("files", file);
    }

    for (const attachment of addedSolutionAttachments) {
      formData.append("addedSolutionAttachments", attachment);
    }

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/update-quiz-question/" + questionId,
      (res) => {
        res = res.data;
        if (res.length !== 0) {
          //alert(JSON.stringify(res));
          alert("Cập nhật thành công");
        } else {
          alert("Cập nhật thất bại");
        }
        history.push("/edu/course/detail/" + courseId);
      },
      {
        onError: (error) => {
          alert("Cập nhật thất bại");
        },
      },
      formData,
      config
    );

    //let chapter = await authPost(dispatch, token, '/create-quiz-question', body);
    //console.log('Create chapter success, chapter = ',chapter);
  }

  function getListTagOfQuiz() {
    // setLoading(true);
    let successHandler = (res) => {
      setChooseTags(res.data.map((item) => item.tagName));
      setLoading(false);
    };
    let errorHandlers = {
      onError: () => {
        errorNoti("Đã xảy ra lỗi khi tải dữ liệu", true);
        setLoading(false);
      },
    };
    request(
      "GET",
      `/get-tags-of-quiz/${questionId}`,
      successHandler,
      errorHandlers
    );
  }

  useEffect(() => {
    getQuizContent();
    // getListTagOfCourse();
    getListTagOfQuiz();
    // console.log('Create chapter of course ' + courseId);
  }, []);

  const handleDeleteImageAttachment = async (fileId) => {
    const newFileArray = fetchedImageArray.filter((file) => file.id !== fileId);
    setFetchedImageArray(newFileArray);
  };

  const handleChange = (event) => {
    const {
      target: { value },
    } = event;
    setChooseTags(
      // On autofill we get a stringified value.
      typeof value === "string" ? value.split(",") : value
    );
  };

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      {!initState ? (
        <div />
      ) : (
        <Card>
          <CardContent>
            <Typography variant="h5" component="h2">
              Nội dung bài tập
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

                <InputLabel id="demo-multiple-name-label">Tags</InputLabel>
                <Select
                  // id="demo-multiple-chip"
                  label="Tags"
                  multiple
                  required
                  value={chooseTags}
                  onChange={handleChange}
                  className={classes.selectBox}
                  input={
                    <OutlinedInput id="select-multiple-chip" label="Chip" />
                  }
                  placeholder="Tags"
                  renderValue={(selected) => (
                    <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
                      {selected.map((value) => (
                        <Chip key={value} label={value} />
                      ))}
                    </Box>
                  )}
                  MenuProps={MenuProps}
                >
                  {tags.map((tag) => (
                    <MenuItem
                      key={tag}
                      value={tag}
                      style={getStyles(tag, tags, theme)}
                    >
                      {tag}
                    </MenuItem>
                  ))}
                </Select>

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
                getFileRemovedMessage={(fileName) =>
                  `Tệp ${fileName} đã loại bỏ`
                }
                getFileLimitExceedMessage={(filesLimit) =>
                  `Vượt quá số lượng tệp tối đa được cho phép. Chỉ được phép tải lên tối đa ${filesLimit} tệp.`
                }
                alertSnackbarProps={{
                  anchorOrigin: { vertical: "bottom", horizontal: "right" },
                  autoHideDuration: 1800,
                }}
                onChange={(files) => handleAttachmentFiles(files)}
              ></DropzoneArea>
              {fetchedImageArray.length !== 0 &&
                fetchedImageArray.map((file) => (
                  <div key={file.id} className={classes.imageContainer}>
                    <div className={classes.imageWrapper}>
                      <HighlightOffIcon
                        className={classes.buttonClearImage}
                        onClick={() => handleDeleteImageAttachment(file.id)}
                      />
                      <img
                        src={`data:image/jpeg;base64,${file.url}`}
                        alt="quiz test"
                        className={classes.imageQuiz}
                      />
                    </div>
                  </div>
                ))}
              <Divider style={{ margin: "60px 0" }} />
              <div>
                <Typography variant="h6" style={{ marginBottom: "10px" }}>
                  Hướng dẫn làm bài
                </Typography>
                <RichTextEditor
                  content={solutionContent}
                  onContentChange={(content) => setSolutionContent(content)}
                />
                {solutionAttachments.map((attachment, index) => (
                  <div
                    style={{
                      width: "568px",
                      height: "300px",
                      position: "relative",
                      marginTop: "10px",
                    }}
                  >
                    <HighlightOffIcon
                      style={{ position: "absolute", top: "5px", right: "5px" }}
                      className={classes.buttonClearImage}
                      onClick={() => removeOldSolutionAttachments(index)}
                    />
                    <FilePreview file={attachment} width="568" height="300" />
                  </div>
                ))}
                <FileUploader
                  onChange={(files) => setAddedSolutionAttachments(files)}
                  multiple
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
              onClick={() => history.push("/edu/course/detail/" + courseId)}
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

export default CreateQuizOfCourse;
