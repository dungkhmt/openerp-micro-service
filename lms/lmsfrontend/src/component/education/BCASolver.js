import React, {useEffect, useState} from "react";
import {makeStyles} from "@material-ui/core/styles";
import {useDispatch, useSelector} from "react-redux";
import {authGet} from "../../api";
import {Button, Card, CardContent, InputLabel, Typography} from "@material-ui/core";
import {API_URL} from "../../config/config";
import CircularProgress from "@material-ui/core/CircularProgress";
import MaterialTable from "material-table";
import Select from "@material-ui/core/Select";
import MenuItem from "@material-ui/core/MenuItem";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: "25ch",
    },
  },
}));

const columns = [
  { title: "Mã lớp", field: "classId" },
  { title: "Mã học phần", field: "courseId" },
  { title: "Tên lớp", field: "className" },
  { title: "Loại lớp", field: "classType" },
  { title: "Ca học", field: "sessionId" },
  { title: "Bộ môn", field: "departmentId" },
  { title: "Học kì", field: "semesterId" },
];

export default function BCASolver(props) {
  const classes = useStyles();
  const dispatch = useDispatch();
  const token = useSelector((state) => state.auth.token);
  const [semesterList, setSemesterList] = useState([]);
  const classFileInput = React.createRef();
  const teacherFileInput = React.createRef();
  const [semester, setSemester] = useState();
  const [classFile, setClassFile] = React.useState();
  const [teacherFile, setTeacherFile] = React.useState();
  const [progressing, setProgressing] = React.useState(false);
  const [executed, setExecuted] = React.useState(false);
  const [exeResult, setExeResult] = React.useState({});

  const openClassFileBrowser = () => {
    classFileInput.current.click();
  };

  const openTeacherFileBrowser = () => {
    teacherFileInput.current.click();
  };

  const classFileHandler = (event) => {
    setClassFile(event.target.files[0]);
  };

  const teacherFileHandler = (event) => {
    setTeacherFile(event.target.files[0]);
  };

  const onSaveClassFileHandler = (event) => {
    if (semester === null) {
      return;
    }
    setProgressing(true);
    var formData = new FormData();
    formData.append("file", classFile);
    fetch(API_URL + "/edu/upload/class/" + semester, {
      method: "POST",
      headers: { "X-Auth-Token": token },
      body: formData,
    }).then((res) => {
      if (res.ok) {
        console.log(res.data);
        setProgressing(false);
        alert("File uploaded successfully.");
      }
    });
  };

  const onSaveTeacherFileHandler = (event) => {
    if (semester === null) {
      return;
    }
    setProgressing(true);
    var formData = new FormData();
    formData.append("file", teacherFile);
    fetch(API_URL + "/edu/upload/course-for-teacher", {
      method: "POST",
      headers: { "X-Auth-Token": token },
      body: formData,
    }).then((res) => {
      if (res.ok) {
        console.log(res.data);
        setProgressing(false);
        alert("File uploaded successfully.");
      }
    });
  };

  const executeAssignment = (event) => {
    if (semester === null) {
      return;
    }
    setProgressing(true);
    fetch(API_URL + "/edu/execute-class-teacher-assignment/" + semester, {
      method: "GET",
      headers: { "X-Auth-Token": token },
    })
      .then((res) => res.json())
      .then((res) => {
        setProgressing(false);
        alert("Successfully executing.");
        console.log(res);
        setExecuted(true);
        setExeResult(res);
      });
  };

  useEffect(() => {
    authGet(dispatch, token, "/edu/get-all-semester").then((res) => {
      console.log(res);
      setSemesterList(res);
    });
  }, []);

  return (
    <div className={classes.root}>
      <Typography variant="h5" component="h2">
        Cấu hình bộ giải
      </Typography>
      <Card>
        <form>
          <div>
            <InputLabel htmlFor="semester">Học kì</InputLabel>
            <Select
              label="Học kì"
              labelId="semester"
              onChange={(event) => {
                setSemester(event.target.value);
              }}
              displayEmpty
              name="semester"
            >
              {semesterList.map((item) => {
                return (
                  <MenuItem value={item.semesterId}>{item.semesterId}</MenuItem>
                );
              })}
            </Select>
          </div>
          <div>
            <Typography>Tải lên danh sách môn học và giáo viên:</Typography>
          </div>
          <div>
            <Button onClick={openTeacherFileBrowser} color="primary">
              Tải lên
            </Button>
            <Button onClick={onSaveTeacherFileHandler} color="primary">
              Lưu
            </Button>
            <input
              type="file"
              enctype="multipart/form-data"
              hidden
              onChange={teacherFileHandler}
              ref={teacherFileInput}
              onClick={(event) => {
                event.target.value = null;
              }}
            ></input>
          </div>
          <div>
            <Typography>Tải lên danh sách lớp:</Typography>
          </div>
          <div>
            <Button onClick={openClassFileBrowser} color="primary">
              Tải lên
            </Button>
            <Button onClick={onSaveClassFileHandler} color="primary">
              Lưu
            </Button>
            <input
              type="file"
              enctype="multipart/form-data"
              hidden
              onChange={classFileHandler}
              ref={classFileInput}
              onClick={(event) => {
                event.target.value = null;
              }}
            ></input>
          </div>
        </form>
        <div>
          <Button
            disabled={progressing}
            variant="contained"
            color="primary"
            onClick={executeAssignment}
          >
            {progressing ? <CircularProgress /> : "Phân công"}
          </Button>
        </div>
        {executed && (
          <CardContent>
            {exeResult.classesNo >= exeResult.sucessNo && (
              <MaterialTable
                title={
                  <div>
                    <div>
                      <Typography>
                        Số lượng lớp tải lên: {" " + exeResult.classesNo}
                      </Typography>
                    </div>
                    <div>
                      <Typography>
                        Số lượng lớp được phân công: {" " + exeResult.sucessNo}
                      </Typography>
                    </div>
                    <div>
                      <Typography>
                        Tỷ lệ phân công thành công:{" "}
                        {" " + exeResult.sucessRate * 100 + "%"}
                      </Typography>
                    </div>
                    <div>
                      <Typography>
                        Danh sách các lớp chưa được phân công:
                      </Typography>
                    </div>
                  </div>
                }
                columns={columns}
                data={exeResult.exception}
              />
            )}
          </CardContent>
        )}
      </Card>
    </div>
  );
}
