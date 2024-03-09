import DateFnsUtils from "@date-io/date-fns";
import Button from "@mui/material/Button";
import Card from "@mui/material/Card";
import CardActions from "@mui/material/CardActions";
import CardContent from "@mui/material/CardContent";
import MenuItem from "@mui/material/MenuItem";
import { makeStyles } from "@material-ui/core/styles";
import { Container, Grid, TextField } from "@mui/material";
import Typography from "@mui/material/Typography";

import { CircularProgress } from "@mui/material";
import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import { request } from "../../../api";
import { errorNoti, successNoti } from "utils/notification";

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

export default function TeacherCreateCourse() {
  const classes = useStyles();
  const history = useHistory();

  const [isRequesting, setIsRequesting] = useState(false);

  const [courseId, setCourseId] = useState(null);
  const [courseName, setCourseName] = useState(null);
  const [credit, setCredit] = useState(null);

  const [invalidCourseId, setInvalidCourseId] = useState(false);
  let errorHandlers = {
    onError: (e) => {
      console.log(e);
      errorNoti("Thêm mới thất bại", 3000);
    },
  };
  let successHandler = () => {
    successNoti("Thêm mới thành công", 3000);
    history.push("/edu/teacher/course/list");
  };

  const handleSubmit = () => {
    if (!courseId || !courseName || !credit) {
      errorNoti("Cần thêm đủ dữ liệu", 3000);
      return;
    } else {
      request("POST", "/edu/course/create", successHandler, errorHandlers, {
        courseId,
        courseName,
        credit,
      });
    }
  };

  const getAllCourse = () => {
    request("get", "/edu/class/get-all-courses", (res) => {
      res = res.data;
      console.log(res);
      setCoursePool(res);
    });
  };

  const getAllSemester = () => {
    request("get", "/edu/class/get-all-semesters", (res) => {
      res = res.data;
      console.log(res);
      setSemesterPool(res);
    });
  };

  useEffect(() => {
    getAllCourse();
    // getAllDepartment();
    getAllSemester();
  }, []);

  const onCourseIdChange = (event) => {
    let id = event.target.value;
    setCourseId(id);
  };

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Card>
        <CardContent>
          <Typography
            variant="h5"
            component="h2"
            style={{ marginBottom: "20px" }}
          >
            Thêm mới môn học
          </Typography>
          <form courseName={classes.root} noValidate autoComplete="off">
            {/* {invalidCourseId && (
              <Typography variant="h5" component="h5" color="error">
                Không tồn tại môn phần {" " + courseId}
              </Typography>
            )} */}
            <div>
              <Grid container rowSpacing={2} spacing={2}>
                <Grid item xs={4}>
                  <TextField
                    required
                    id="courseId"
                    label="Mã học phần"
                    placeholder="Nhập mã học phần"
                    value={courseId}
                    onChange={onCourseIdChange}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                </Grid>
                <Grid item xs={8}>
                  <TextField
                    required
                    id="courseName"
                    label="Tên học phần"
                    value={courseName}
                    style={{ width: "40ch" }}
                    onChange={(event) => {
                      setCourseName(event.target.value);
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                </Grid>
                <Grid item xs={6}>
                  <TextField
                    required
                    id="credit"
                    label="Số tín chỉ"
                    value={credit}
                    style={{ width: "40ch" }}
                    onChange={(event) => {
                      setCredit(event.target.value);
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                  <Typography>*Must be a number</Typography>
                </Grid>
              </Grid>
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
            onClick={() => history.push("/edu/teacher/course/list")}
          >
            Hủy
          </Button>
        </CardActions>
      </Card>
    </MuiPickersUtilsProvider>
  );
}
