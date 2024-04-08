import DateFnsUtils from "@date-io/date-fns";
import Button from "@material-ui/core/Button";
import Card from "@material-ui/core/Card";
import CardActions from "@material-ui/core/CardActions";
import CardContent from "@material-ui/core/CardContent";
import MenuItem from "@material-ui/core/MenuItem";
import {makeStyles} from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import Typography from "@material-ui/core/Typography";

import {CircularProgress} from "@material-ui/core";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(4),
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: 200,
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

const columns = [
  { title: "Mã lớp", field: "classId" },
  { title: "Mã học phần", field: "courseId" },
  { title: "Tên lớp", field: "className" },
  { title: "Loại lớp", field: "classType" },
  { title: "Ca học", field: "sessionId" },
  { title: "Bộ môn", field: "departmentId" },
  { title: "Học kì", field: "semesterId" },
];

export default function CreateClass() {
  const classes = useStyles();
  const history = useHistory();

  const [isRequesting, setIsRequesting] = useState(false);

  const [classId, setClassId] = useState(null);
  const [classType, setClassType] = useState(null);
  const [courseId, setCourseId] = useState(null);
  const [className, setClassName] = useState(null);
  const [sessionId, setSessionId] = useState(null);
  const [departmentId, setDepartmentId] = useState(null);
  const [semesterId, setSemesterId] = useState(null);

  const [coursePool, setCoursePool] = useState([]);
  const [semesterPool, setSemesterPool] = useState([]);
  const departmentPool = [
    { departmentId: "ATTT", departmentName: "An toàn thông tin" },
    { departmentId: "CNPM", departmentName: "Công nghệ phần mềm" },
    { departmentId: "HTTT", departmentName: "Hệ thống thông tin" },
    { departmentId: "KHMT", departmentName: "Khoa học máy tính" },
    { departmentId: "KTMT", departmentName: "Kĩ thuật máy tính" },
    { departmentId: "TTMMT", departmentName: "Truyền thông và mạng máy tính" },
    { departmentId: "TTMT", departmentName: "Trung tâm máy tính" },
  ];
  const classTypePool = [
    { type: "LT" },
    { type: "BT" },
    { type: "LT+BT" },
    { type: "TN" },
    { type: "DA" },
  ];

  const [invalidCourseId, setInvalidCourseId] = useState(false);

  const getAllCourse = () => {
    request("get", "/edu/class/get-all-courses", (res) => {
      res = res.data;
      console.log(res);
      setCoursePool(res);
    });
  };

  //   const getAllDepartment = () => {
  //     authGet(dispatch, token, "/edu/get-all-department").then((res) => {
  //       console.log(res);
  //       setDepartmentPool(res);
  //     });
  //   };

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

    if (id === "") {
      setInvalidCourseId(false);
      setClassName(null);
      return;
    }

    let ok = false;
    coursePool.forEach((course) => {
      if (course.courseId === id) {
        setClassName(course.courseName);
        console.log(course.courseName);
        ok = true;
      }
    });
    if (!ok) {
      setInvalidCourseId(true);
      setClassName(null);
    } else {
      setInvalidCourseId(false);
    }
  };

  return (
    <MuiPickersUtilsProvider utils={DateFnsUtils}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2">
            Thêm mới lớp học
          </Typography>
          <form className={classes.root} noValidate autoComplete="off">
            {invalidCourseId && (
              <Typography variant="h5" component="h5" color="error">
                Không tồn tại mã học phần {" " + courseId}
              </Typography>
            )}
            <div>
              <div>
                <TextField
                  autoFocus
                  required
                  id="classId"
                  label="Mã lớp"
                  placeholder="Nhập mã lớp"
                  value={classId}
                  onChange={(event) => {
                    setClassId(event.target.value);
                  }}
                  InputLabelProps={{
                    shrink: true,
                  }}
                />

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

                <TextField
                  required
                  id="className"
                  label="Tên học phần"
                  value={className}
                  style={{ width: "40ch" }}
                  onChange={(event) => {
                    setClassName(event.target.value);
                  }}
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
              </div>
              <div>
                <TextField
                  required
                  id="select-class-type"
                  select
                  label="Loại lớp"
                  value={classType}
                  onChange={(event) => {
                    setClassType(event.target.value);
                  }}
                >
                  {classTypePool.map((item) => (
                    <MenuItem key={item.type} value={item.type}>
                      {item.type}
                    </MenuItem>
                  ))}
                </TextField>
                <TextField
                  required
                  id="select-semester"
                  select
                  label="Học kì"
                  value={semesterId}
                  onChange={(event) => {
                    setSemesterId(event.target.value);
                  }}
                >
                  {semesterPool.map((item) => (
                    <MenuItem key={item.id} value={item.id}>
                      {item.id}
                    </MenuItem>
                  ))}
                </TextField>
                <TextField
                  required
                  id="select-department"
                  select
                  label="Bộ môn"
                  style={{ width: "40ch" }}
                  value={departmentId}
                  onChange={(event) => {
                    setDepartmentId(event.target.value);
                  }}
                >
                  {departmentPool.map((item) => (
                    <MenuItem key={item.departmentId} value={item.departmentId}>
                      {item.departmentName}
                    </MenuItem>
                  ))}
                </TextField>
              </div>
            </div>
          </form>
        </CardContent>
        <CardActions>
          <Button
            disabled={isRequesting}
            variant="contained"
            color="primary"
            style={{ marginLeft: "45px" }}
            onClick={() => {
              alert("Under construction");
            }}
          >
            {isRequesting ? <CircularProgress /> : "Lưu"}
          </Button>
          <Button
            variant="contained"
            onClick={() => history.push("/edu/classes-list")}
          >
            Hủy
          </Button>
        </CardActions>
      </Card>
    </MuiPickersUtilsProvider>
  );
}
