import DateFnsUtils from "@date-io/date-fns";
import {CircularProgress} from "@material-ui/core";
import Button from "@material-ui/core/Button";
import Card from "@material-ui/core/Card";
import CardActions from "@material-ui/core/CardActions";
import CardContent from "@material-ui/core/CardContent";
import MenuItem from "@material-ui/core/MenuItem";
import {makeStyles} from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import Typography from "@material-ui/core/Typography";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import React, {useEffect, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {useHistory} from "react-router-dom";
import {failed} from "../../../action/Auth";
import {authPost, BASE_URL} from "../../../api";
import withScreenSecurity from "../../withScreenSecurity";

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

function ClassCreate() {
  const classes = useStyles();
  const history = useHistory();
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const [isRequesting, setIsRequesting] = useState(false);

  const [classId, setClassId] = useState(null);
  const [classType, setClassType] = useState(null);
  const [courseId, setCourseId] = useState(null);

  const [departmentId, setDepartmentId] = useState(null);
  const [semesterId, setSemesterId] = useState(null);

  const [coursePool, setCoursePool] = useState([]);
  const [semesterPool, setSemesterPool] = useState([]);
  const [departmentPool, setDepartmentPool] = useState([]);

  /*
    const departmentPool = [
      { departmentId: "ATTT", departmentName: "An toàn thông tin" },
      { departmentId: "CNPM", departmentName: "Công nghệ phần mềm" },
      { departmentId: "HTTT", departmentName: "Hệ thống thông tin" },
      { departmentId: "KHMT", departmentName: "Khoa học máy tính" },
      { departmentId: "KTMT", departmentName: "Kĩ thuật máy tính" },
      { departmentId: "TTMMT", departmentName: "Truyền thông và mạng máy tính" },
      { departmentId: "TTMT", departmentName: "Trung tâm máy tính" },
      { departmentId: "KCNTT", departmentName: "Viện CNTT&TT" },
    ];
    */
  const classTypePool = [
    {type: "LT"},
    {type: "BT"},
    {type: "LT+BT"},
    {type: "TN"},
    {type: "DA"},
  ];
  const [invalidCourseId, setInvalidCourseId] = useState(false);
  const [invalidClassId, setInvalidClassId] = useState(false);
  const [invalidRequirementField, setInvalidRequirementField] = useState(false);


  const getAllCourses = () => {
    fetch(BASE_URL + "/edu/class/get-all-courses", {
      method: "GET",
      headers: {"Content-Type": "application/json", "X-Auth-Token": token},
    })
      .then((response) => response.json())
      .then(
        (response) => {
          setCoursePool(response);
        },
        (error) => {
          setCoursePool([]);
        }
      );
  };

  const getAllDepartments = () => {
    fetch(BASE_URL + "/edu/class/get-all-departments", {
      method: "GET",
      headers: {"Content-Type": "application/json", "X-Auth-Token": token},
    })
      .then((response) => response.json())
      .then(
        (response) => {
          setDepartmentPool(response);
        },
        (error) => {
          setDepartmentPool([]);
        }
      );
  };

  const getAllSemesters = () => {
    fetch(BASE_URL + "/edu/class/get-all-semesters", {
      method: "GET",
      headers: {"Content-Type": "application/json", "X-Auth-Token": token},
    })
      .then((response) => response.json())
      .then(
        (response) => {
          console.log(response);
          let arr = [];
          response.forEach((d) => {
            arr.push(d);
          });
          setSemesterPool(arr);

          //setSemesterPool(response);
          //console.log('getDepartmentList = ',departments);
        },
        (error) => {
          setSemesterPool([]);
        }
      );

    /*
        authGet(dispatch, token, "/edu/get-all-semester").then((res) => {
          console.log(res);
          setSemesterPool(res);
        });
        */
  };

  useEffect(() => {
    getAllCourses();
    getAllDepartments();
    getAllSemesters();
    console.log("departments = ", departmentPool);
  }, []);

  const onClassIdChange = (event) => {
    setClassId(event.target.value);
    /*let classIdTemp = event.target.value;
    
      if (classIdTemp === null || classIdTemp.trim() === "" || isNormalInteger(classIdTemp))
        setInvalidClassId(false);          
      else
        setInvalidClassId(true);      
        */
    // remove checking digits
    setInvalidClassId(false);
  };

  // const onCourseIdChange = (event) => {
  //   let id = event.target.value;
  //   setCourseId(id);

  //   if (id === "") {
  //     setInvalidCourseId(false);
  //     setClassName(null);
  //     return;
  //   }

  //   let ok = false;
  //   coursePool.forEach((course) => {
  //     if (course.courseId === id) {
  //       setClassName(course.courseName);
  //       console.log(course.courseName);
  //       ok = true;
  //     }
  //   });
  //   if (!ok) {
  //     setInvalidCourseId(true);
  //     setClassName(null);
  //   } else {
  //     setInvalidCourseId(false);
  //   }
  // };

  const handleSubmit = () => {
    const data = {
      classCode: classId,
      semesterId: semesterId,
      courseId: courseId,
      classType: classType,
      departmentId: departmentId,
    };

    let invalidRequirementFieldTemp =
      classId === null ||
      classId.trim() === "" ||
      semesterId === null ||
      courseId === null ||
      classType === null ||
      departmentId === null;

    setInvalidRequirementField(invalidRequirementFieldTemp);
    if (invalidClassId) {
      alert("Mã học phần không thỏa mãn");
      return;
    }

    if (invalidRequirementFieldTemp) {
      alert("Điền các trường còn trống");
      return;
    }

    setIsRequesting(true);
    authPost(dispatch, token, "/edu/class/add", data)
      .then(
        (res) => {
          setIsRequesting(false);
          if (res.message === "duplicate") {
            alert("Mã lớp đã tồn tại");
            return "duplicate";
          } else if (res.status === 401) {
            dispatch(failed());
            throw Error("Unauthorized");
          } else if (res.status === 409) {
            alert("User exits!!");
          } else if (res.status === 201) {
            return res.json();
          }
        },
        (error) => {
          console.log(error);
        }
      )
      .then((res) => {
        if (res !== "duplicate") history.push("/edu/teacher/class/list");
      });
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
            {invalidClassId && (
              <Typography variant="h5" component="h5" color="error">
                Mã học phần không thỏa mãn, chỉ bao gồm số không dấu
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
                  onChange={onClassIdChange}
                  InputLabelProps={{
                    shrink: true,
                  }}
                >
                  ABC
                </TextField>

                <TextField
                  required
                  id="select-course"
                  select
                  label="Học phần"
                  value={courseId}
                  onChange={(event) => {
                    setCourseId(event.target.value);
                  }}
                >
                  {coursePool.map((item) => (
                    <MenuItem key={item.id} value={item.id}>
                      {item.name}({item.id})
                    </MenuItem>
                  ))}
                </TextField>
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
                  style={{width: "40ch"}}
                  value={departmentId}
                  onChange={(event) => {
                    setDepartmentId(event.target.value);
                  }}
                >
                  {departmentPool.map((item) => (
                    <MenuItem key={item.id} value={item.id}>
                      {item.id}
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
            style={{marginLeft: "45px"}}
            onClick={handleSubmit}
          >
            {isRequesting ? <CircularProgress/> : "Lưu"}
          </Button>
          <Button
            variant="contained"
            onClick={() => history.push("/edu/teacher/class/list")}
          >
            Hủy
          </Button>
        </CardActions>
      </Card>
    </MuiPickersUtilsProvider>
  );
}

const screenName = "SCREEN_EDUCATION_TEACHING_MANAGEMENT_TEACHER";
export default withScreenSecurity(ClassCreate, screenName, true);
