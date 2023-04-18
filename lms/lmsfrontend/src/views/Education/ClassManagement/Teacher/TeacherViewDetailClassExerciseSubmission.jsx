import {request} from "../../../../api";
import React, {useEffect, useRef, useState} from "react";
import {Avatar, Card, CardContent, CardHeader, Typography,} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {drawerWidth} from "../../../../assets/jss/material-dashboard-react";
import {FcConferenceCall,} from "react-icons/fc";
import Button from "@material-ui/core/Button";
import ReactExport from "react-data-export";
import MaterialTable from "material-table";
import {localization, tableIcons} from "../../../../utils/MaterialTableUtils";
import AssignmentSubmissionListOfClass
  from "../../../../component/education/classmanagement/teacher/AssignmentSubmissionListOfClass";

const useStyles = makeStyles((theme) => ({
  root: {
    // flexGrow: 1,
    margin: "auto",
    width: `calc(100vw - ${drawerWidth + theme.spacing(4) * 2 + 1}px)`,
    backgroundColor: theme.palette.background.paper,
  },
  card: {
    marginTop: theme.spacing(2),
  },
}));
const ExcelFile = ReactExport.ExcelFile;
const ExcelSheet = ReactExport.ExcelFile.ExcelSheet;

export default function TeacherViewDetailClassExerciseSubmission(props) {
  const classes = useStyles();
  //const params = useParams();
  const classId = props.classId;
  //const history = useHistory();
  const [studentAssignmentList, setStudentAssignmentList] = useState([]);
  const [fetchedStudentAssignment, setFetchedStudentAssignment] =
    useState(false);

  const [classDetail, setClassDetail] = useState({});
  //const studentTableRef = useRef(null);
  const studentAssignTableRef = useRef(null);

  const TableBorderStyle = "medium";
  const TableHeaderStyle = {
    style: {
      font: { sz: "14", bold: true },
      alignment: { vertical: "center", horizontal: "center" },
      border: {
        top: { style: TableBorderStyle },
        bottom: { style: TableBorderStyle },
        left: { style: TableBorderStyle },
        right: { style: TableBorderStyle },
      },
    },
  };

  const TableCellStyle = {
    style: {
      font: { sz: "14" },
      alignment: { vertical: "center", horizontal: "center" },
      border: {
        top: { style: TableBorderStyle },
        bottom: { style: TableBorderStyle },
        left: { style: TableBorderStyle },
        right: { style: TableBorderStyle },
      },
    },
  };
  const stuAssignCols = [
    {
      field: "studentName",
      title: "Họ và tên sinh viên",
    },
  ].concat(
    !fetchedStudentAssignment
      ? []
      : !studentAssignmentList.length
      ? []
      : studentAssignmentList[0].assignmentList.map((assignment, index) => {
          return {
            field: "assignmentList[" + index + "].assignmentStatus",
            title: assignment.assignmentName,
          };
        }),
    [
      {
        field: "totalSubmitedAssignment",
        //field: "totalSubmitedAssignment",
        title: "Tổng số bài nộp",
      },
    ]
  );

  const DataSet = [
    {
      columns: [
        {
          title: "Họ và tên sinh viên",
          ...TableHeaderStyle,
          width: { wch: "Họ và tên sinh viên".length },
        },
      ].concat(
        !fetchedStudentAssignment
          ? []
          : !studentAssignmentList.length
          ? []
          : studentAssignmentList[0].assignmentList.map((assignment) => {
              return {
                title: assignment.assignmentName,
                ...TableHeaderStyle,
                width: { wch: assignment.assignmentName.length + 3 },
              };
            }),
        [
          {
            title: "Tổng số bài nộp",
            ...TableHeaderStyle,
            width: { wch: "Tổng số bài nộp".length },
          },
        ]
      ),
      data: !fetchedStudentAssignment
        ? []
        : studentAssignmentList.map((data) => {
            return [{ value: data.studentName, ...TableCellStyle }].concat(
              data.assignmentList.map((data2) => {
                return { value: data2.assignmentStatus, ...TableCellStyle };
              }),
              [{ value: data.totalSubmitedAssignment, ...TableCellStyle }]
            );
          }),
    },
  ];
  const getStudentAssignment = () => {
    request(
      // token,
      // history,
      "get",
      `/edu/class/${classId}/all-student-assignments/teacher`,
      (res) => {
        setStudentAssignmentList(res.data);
        setFetchedStudentAssignment(true);
      }
    );
  };

  useEffect(() => {
    //getClassDetail();
    //getAssigns();
    getStudentAssignment();
    //getStudents("register");
    //getStudents();
  }, []);

  return (
    <div>
      <Card>
        <CardContent>
          <AssignmentSubmissionListOfClass classId={classId}/>
        </CardContent>
      </Card>


      <Card className={classes.card}>
        {/* <CardActionArea disableRipple onClick={onClickStuAssignCard}> */}
        <CardHeader
          avatar={
            <Avatar style={{ background: "white" }}>
              {/*#ffeb3b <PeopleAltRoundedIcon /> */}
              <FcConferenceCall size={40} />
            </Avatar>
          }
          title={<Typography variant="h5">Danh sách nộp bài tập</Typography>}
          action={
            studentAssignmentList.length !== 0 ? (
              <ExcelFile
                filename={"Danh sách nộp bài tập lớp " + classDetail.code}
                element={
                  <Button
                    variant="contained"
                    color="secondary"
                    style={{ marginLeft: "0px" }}
                  >
                    Xuất Excel
                  </Button>
                }
              >
                <ExcelSheet
                  dataSet={DataSet}
                  name={"Danh sách nộp bài tập lớp " + classDetail.code}
                />
              </ExcelFile>
            ) : null
          }
        />
        {/* </CardActionArea>
          <Collapse in={openStuAssignCard} timeout="auto"> */}
        <CardContent>
          {/* {studentAssignmentList.length !== 0 ? (
            <ExcelFile
              filename={"Danh sách nộp bài tập lớp " + classDetail.code}
              element={
                <Button
                  variant="contained"
                  color="secondary"
                  style={{ marginLeft: "0px" }}
                >
                  Xuất Excel
                </Button>
              }
            >
              <ExcelSheet
                dataSet={DataSet}
                name={"Danh sách nộp bài tập lớp " + classDetail.code}
              />
            </ExcelFile>
          ) : null} */}
          <MaterialTable
            title=""
            columns={stuAssignCols}
            icons={tableIcons}
            tableRef={studentAssignTableRef}
            localization={localization}
            data={studentAssignmentList}
            components={{
              Toolbar: () => null,
              Container: (props) => <span {...props} elevation={0} />,
            }}
            options={{
              // fixedColumns: {
              //   left: 1,
              //   right: 1,
              // },
              draggable: false,
              filtering: true,
              sorting: true,
              search: false,
              pageSize: 10,
              debounceInterval: 500,
              toolbarButtonAlignment: "left",
              // exportButton: true,
              // exportFileName: "Danh sách nộp bài tập lớp " + classDetail.code,
              // exportDelimiter: ",",
            }}
          />
        </CardContent>
        {/* </Collapse> */}
      </Card>
    </div>
  );
}
