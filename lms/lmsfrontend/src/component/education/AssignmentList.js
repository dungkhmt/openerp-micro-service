import {FormControl, Typography} from "@material-ui/core";
import Button from "@material-ui/core/Button";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import Select from "@material-ui/core/Select";
import {makeStyles} from "@material-ui/core/styles";
import {request} from "api";
import MaterialTable from "material-table";
import React, {useEffect} from "react";
import XLSX from "xlsx";

const useStyles = makeStyles((theme) => ({
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
  },
}));

export default function AssignmentList(props) {
  const classes = useStyles();

  // pool
  const [semester, setSemester] = React.useState([]);
  const [teacher, setTeacher] = React.useState([
    { teacherId: " ", teacherName: "Tất cả", email: "Tất cả" },
  ]);

  // selected
  const [semesterQuery, setSemesterQuery] = React.useState();
  const [teacherQuery, setTeacherQuery] = React.useState(" ");

  // query result
  const [assignment, setAssignment] = React.useState([]);

  const columns = [
    { title: "Mã lớp", field: "classId" },
    { title: "Mã học phần", field: "courseId" },
    { title: "Tên học phần", field: "courseName" },
    { title: "Loại lớp", field: "classType" },
    { title: "Số tín chỉ", field: "credit" },
    { title: "Tên giảng viên", field: "teacherName" },
    { title: "Email", field: "email" },
    { title: "Ca học", field: "sessionId" },
  ];

  const semesterChange = (event) => {
    console.log(event.target.value);
    setSemesterQuery(event.target.value);
  };

  const teacherChange = (event) => {
    console.log(event.target.value);
    setTeacherQuery(event.target.value);
  };

  const onQueryClick = (event) => {
    if (semesterQuery === null) {
      return;
    }

    request(
      "get",
      "/edu/get-all-assignment/" + semesterQuery + "/" + teacherQuery,
      (response) => {
        setAssignment(response.data);
      }
    );
  };

  useEffect(() => {
    request("get", "/edu/get-all-semester", (response) => {
      response = response.data;
      console.log(response);
      setSemester(response);
    });

    request("get", "/edu/get-all-teachers", (response) => {
      response = response.data;
      console.log(response);
      setTeacher([...teacher, ...response]);
    });
  }, []);

  const downloadHandler = (event) => {
    if (assignment.length === 0) {
      return;
    }
    var wbcols = [
      { wpx: 50 },
      { wpx: 50 },
      { wpx: 200 },
      { wpx: 50 },
      { wpx: 50 },
      { wpx: 200 },
      { wpx: 100 },
      { wpx: 200 },
    ];

    var data = assignment.map((item) => ({
      "Mã lớp": item.classId,
      "Mã học phần": item.courseId,
      "Tên học phần": item.courseName,
      "Loại lớp": item.classType,
      "Số tín chỉ": item.credit,
      "Tên giảng viên": item.teacherName,
      Email: item.email,
      "Ca học": item.sessionId,
    }));

    var sheet = XLSX.utils.json_to_sheet(data);
    var wb = XLSX.utils.book_new();
    sheet["!cols"] = wbcols;

    XLSX.utils.book_append_sheet(wb, sheet, "assignment");
    XLSX.writeFile(wb, semesterQuery + "-assignment.xlsx");
  };

  return (
    <div>
      <div>
        <Typography variant="h5" component="h2">
          Tra cứu danh sách phân công giảng viên các kì
        </Typography>
      </div>
      <div>
        <FormControl className={classes.formControl}>
          <InputLabel htmlFor="semester">Học kì</InputLabel>
          <Select
            labelId="semester"
            value={semesterQuery}
            onChange={semesterChange}
            displayEmpty
            name="semester"
          >
            {semester.map((item) => {
              return (
                <MenuItem value={item.semesterId}>{item.semesterName}</MenuItem>
              );
            })}
          </Select>
        </FormControl>

        <FormControl className={classes.formControl}>
          <InputLabel htmlFor="teacherId">Giảng viên</InputLabel>
          <Select
            labelId="teacherId"
            value={teacherQuery}
            onChange={teacherChange}
            displayEmpty
            name="teacherQuery"
          >
            {teacher.map((item) => {
              return (
                <MenuItem value={item.teacherId}>{item.teacherName}</MenuItem>
              );
            })}
          </Select>
        </FormControl>

        <FormControl className={classes.formControl}>
          <Button onClick={onQueryClick} color="primary">
            Tra cứu
          </Button>
          <Button onClick={downloadHandler} color="primary">
            Tải xuống
          </Button>
        </FormControl>
      </div>
      <div>
        <MaterialTable
          title={
            assignment.length > 0
              ? "Danh sách phân công giảng viên kì " + [semesterQuery]
              : "Danh sách phân công giảng viên"
          }
          columns={columns}
          data={assignment}
        />
      </div>
    </div>
  );
}
