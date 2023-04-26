import {request} from "../../../../api";
import React, {useEffect, useState} from "react";
import {makeStyles} from "@material-ui/core/styles";
import {drawerWidth} from "../../../../assets/jss/material-dashboard-react";
import StandardTable from "../../../table/StandardTable";
import ExcelExporter from "../../../common/ExcelExporter";

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

export default function AssignmentSubmissionListOfClass(props) {
  const classes = useStyles();
  const classId = props.classId;
  const [classDetail, setClassDetail] = useState({});
  const [assignmentSubmissions, setAssignmentSubmissions] = useState([]);

  useEffect(getStudentAssignment, []);

  function getStudentAssignment() {
    request("GET", `/edu/class/${classId}/all-student-assignments/teacher`, (res) => {
      setAssignmentSubmissions(res.data);
    });
  }

  const tableColumns = getAssignmentSubmissionsTableColumns(assignmentSubmissions);
  const actions = [{ icon: () => ExcelExportButton, isFreeAction: true }]

  const excelFilename = `Danh sách nộp bài tập lớp ${classDetail.code}`;
  const excelExportedSubmissions = [{
      columns: getExcelColumnsFromTableColumns(tableColumns),
      data:  assignmentSubmissions.map(mapSubmissionsOfAStudentToExcelDataRow),
  }];

  const ExcelExportButton = (
    <ExcelExporter filename={excelFilename}
                   sheets={[{
                     name: excelFilename,
                     dataSet: excelExportedSubmissions
                   }]} />
  )

  return (
    <StandardTable  title="Danh sách nôp bài tập"
                    columns={tableColumns}
                    data={assignmentSubmissions}
                    hideCommandBar
                    options={{
                      selection: false,
                      search: true,
                      sorting: true
                    }}
                    actions={actions}
                    />
  );
}


function getAssignmentSubmissionsTableColumns(submissions) {
  let assignmentList = submissions && submissions.length ? submissions[0].assignmentList : [];
  return [
    { field: "studentName", title: "Họ và tên sinh viên" },
    ...getAssignmentColumns(assignmentList),
    { field: "totalSubmitedAssignment", title: "Tổng số bài nộp" }
  ]
}

function getAssignmentColumns(assignmentList) {
  return assignmentList.map((assignment, index) => ({
    field: `assignmentList[${index}].assignmentStatus`,
    title: assignment.assignmentName
  }));
}

function getExcelColumnsFromTableColumns(tableColumns) {
  return tableColumns.map(tableColumn => ({
    title: tableColumn.title,
    style: EXCEL_HEADER_CELL_STYLE,
    width: { wch: tableColumn.title.length }
  }))
}

function mapSubmissionsOfAStudentToExcelDataRow(submissionsOfAStudent) {
  let submissions = submissionsOfAStudent.assignmentList;
  return [
    { value: submissionsOfAStudent.studentName, style: EXCEL_DATA_CELL_STYLE },
    ...submissions.map(submission => ({ value: submission.assignmentStatus, style: EXCEL_DATA_CELL_STYLE })),
    { value: submissionsOfAStudent.totalSubmitedAssignment, style: EXCEL_DATA_CELL_STYLE }
  ]
}

const EXCEL_CELL_STYLE =  {
  alignment: {
    vertical: "center",
    horizontal: "center"
  },
  border: {
    top: { style: "medium" },
    bottom: { style: "medium" },
    left: { style: "medium" },
    right: { style: "medium" }
  }
}

const EXCEL_DATA_CELL_STYLE = {
  font: { sz: "14" },
  ...EXCEL_CELL_STYLE
}

const EXCEL_HEADER_CELL_STYLE = {
  font: { sz: "14", bold: true },
  ...EXCEL_CELL_STYLE
}
