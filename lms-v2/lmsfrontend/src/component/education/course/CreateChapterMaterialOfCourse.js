import DateFnsUtils from "@date-io/date-fns";
import {Card, CardActions, CardContent, MenuItem, TextField, Typography,} from "@material-ui/core/";
import Button from "@material-ui/core/Button";
import {makeStyles} from "@material-ui/core/styles";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {EditorState} from "draft-js";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
import {errorNoti, successNoti} from "../../../utils/notification";
import AlertDialog from "../../common/AlertDialog";
import Loading from "../../common/Loading";

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

function CreateChapterMaterialOfCourse() {
  const params = useParams();
  const classes = useStyles();
  const chapterId = params.chapterId;
  const [materialName, setMaterialName] = useState(null);
  const [materialType, setMaterialType] = useState(null);
  const [materialTypeList, setMaterialTypeList] = useState([]);
  const [selectedInputFile, setSelectedInputFile] = useState(null);

  const [alertMessage, setAlertMessage] = useState({
    title: "Vui lòng nhập đầy đủ thông tin cần thiết",
    content:
      "Một số thông tin yêu cầu cần phải được điền đầy đủ. Vui lòng kiểm tra lại.",
  });
  const [alertSeverity, setAlertSeverty] = useState("info");
  const [openAlert, setOpenAlert] = useState(false);
  const history = useHistory();

  const [editorState, setEditorState] = useState(EditorState.createEmpty());
  const [isLoading, setIsLoading] = useState(false);

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
    //console.log('handle submit');
    setIsLoading(true);
    let body = {
      chapterId: chapterId,
      materialName: materialName,
      materialType: materialType,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("files", selectedInputFile);

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/edu/class/create-chapter-material-of-course",
      (res) => {
        res = res.data;
        console.log("res = ", res);
        if (res.error) {
          errorNoti("Tạo bài giảng thất bại", true);
        } else {
          successNoti("Tạo bài giảng thành công", true);
          history.push("/edu/teacher/course/chapter/detail/" + chapterId);
        }
        setIsLoading(false);
      },
      {},
      formData,
      config
    );

    //let chapter = await authPost(dispatch, token, '/edu/class/create-chapter-material-of-course', body);
    //history.push("/edu/course/chapter/detail/" + chapterId);
    //edu/teacher/course/chapter/detail/010a357c-eb5b-49a6-93de-ec1aef3695dd
  }

  async function getCourseChapterMaterialTypeList() {
    request(
      "get",
      "/edu/class/get-course-chapter-material-type-list",
      (res) => {
        setMaterialTypeList(res.data);
        console.log("types = ", res.data);
      }
    );
  }

  function onInputFileChange(event) {
    setSelectedInputFile(event.target.files[0]);
  }

  useEffect(() => {
    getCourseChapterMaterialTypeList();
    console.log("Create chapter material of course " + chapterId);
  }, []);
  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      {isLoading ? (
        <Loading />
      ) : (
        <Card>
          <CardContent>
            <Typography variant="h5" component="h2">
              Tạo bài tập
            </Typography>
            <form className={classes.root} noValidate autoComplete="off">
              <div>
                <TextField
                  required
                  id="materialName"
                  label="Tên học liệu"
                  placeholder="Nhập tên học liệu"
                  value={materialName}
                  onChange={(event) => {
                    setMaterialName(event.target.value);
                  }}
                />

                <TextField
                  required
                  id="materialType"
                  select
                  label="Thể Loại"
                  value={materialType}
                  fullWidth
                  onChange={(event) => {
                    setMaterialType(event.target.value);
                    //console.log(problemId,event.target.value);
                  }}
                >
                  {materialTypeList.map((item) => (
                    <MenuItem key={item} value={item}>
                      {item}
                    </MenuItem>
                  ))}
                </TextField>

                <label>Select Input file</label>
                <input type="file" onChange={onInputFileChange} />
                <br></br>
                <br></br>
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

export default CreateChapterMaterialOfCourse;
