import DateFnsUtils from "@date-io/date-fns";
import {
  Chip,
  CircularProgress,
  InputLabel,
  MenuItem,
  OutlinedInput,
  Select,
} from "@mui/material";
import {
  Card,
  CardActions,
  CardContent,
  TextField,
  Typography,
} from "@mui/material";
import Button from "@mui/material/Button";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import { useEffect, useState } from "react";
import { useParams } from "react-router";
import { useHistory } from "react-router-dom";
import { request } from "../../../api";
import AlertDialog from "../../common/AlertDialog";
import { Box } from "@mui/system";
import { errorNoti, successNoti } from "utils/notification";

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
  selectBox: {
    padding: 20,
    minWidth: 150,
    marginRight: 40,
    height: 60,
  },
}));

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

function CreateTagOfCourse() {
  const theme = useTheme();
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
  const [tags, setTags] = useState([]);
  const [chooseTags, setChooseTags] = useState([]);
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
      tagName: quizCourseTopicName,
      courseId: courseId,
    };
    setIsRequesting(true);

    request(
      "post",
      "/create-quiz-tag",
      (res) => {
        setIsRequesting(false);
        successNoti("Tạo thành công");
        console.log("Create topic success, topic = ", res);
        history.push("/edu/course/detail/" + courseId);
      },
      {
        onError: (error) => {
          alert("Dữ liệu không đúng");
        },
      },
      body
    );
  }

  function getListTagOfCourse() {
    // setLoading(true);
    let successHandler = (res) => {
      setTags(res.data.map((item) => item.tagName));
      //   setLoading(false);
    };
    let errorHandlers = {
      onError: () => {
        errorNoti("Đã xảy ra lỗi khi tải dữ liệu", true);
        // setLoading(false);
      },
    };
    request(
      "GET",
      `/get-tags-of-course/${courseId}`,
      successHandler,
      errorHandlers
    );
  }

  useEffect(() => {
    console.log("Create topic of course " + courseId);
    getListTagOfCourse();
  }, []);

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Tạo tag
          </Typography>
          <form className={classes.root} noValidate autoComplete="off">
            <div>
              <InputLabel id="demo-multiple-name-label">
                Các tags đã có
              </InputLabel>
              <Select
                label="tags"
                id="demo-multiple-chip"
                multiple
                value={chooseTags}
                className={classes.selectBox}
                input={<OutlinedInput id="select-multiple-chip" label="Chip" />}
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
              <TextField
                required
                id="quizCourseTopicName"
                label="Tên tag"
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

export default CreateTagOfCourse;
