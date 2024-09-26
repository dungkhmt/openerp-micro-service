import {Card, CardContent} from "@material-ui/core/";
import React, {useEffect, useState} from "react";
import {Link, useHistory} from "react-router-dom";
import {exportQuizsListPdf} from "../TeacherCourseQuizListExportPDF.js";
import {Button} from "@mui/material";
import {errorNoti, successNoti} from "../../../../utils/notification";
import {request} from "../../../../api";
import ExcelExporter from "../../../common/ExcelExporter";
import AddIcon from "@mui/icons-material/Add";
import PropTypes from "prop-types";
import StandardTable from "../../../table/StandardTable";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(theme => ({
  tableWrapper: {
    '& [class^=MTableToolbar-actions]>div>div>span>button': {
      padding: 'unset',
      paddingLeft: '8px'
    },
    '& table thead tr': {
      '& th:nth-child(2)': {
        maxWidth: '100px !important'
      },
      '& th:nth-child(3)': {
        maxWidth: '160px !important'
      },
      '& th:nth-child(6)': {
        maxWidth: '108px !important'
      },
      '& th:nth-child(7)': {
        maxWidth: '140px !important',
        padding: '5px'
      }
    }
  }
}))

export default function TeacherViewQuizListOfCourse(props) {
  const classes = useStyles();
  const courseId = props.courseId;
  const history = useHistory();
  const [quizsOfCourse, setQuizsOfCourse] = useState([]);

  useEffect(getQuizsOfCourse, []);

  function getQuizsOfCourse() {
    const successHandler = res => setQuizsOfCourse(res.data);
    const errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi trong khi tải dữ liệu", true)
    }
    request("GET", `/get-quiz-of-course-sorted-created-time-desc/${courseId}`, successHandler, errorHandlers);
  }

  const excelColumns = [
    { title: "QuestionId", style: EXCEL_HEADER_CELL_STYLE, width: { wch: "50" } },
    { title: "Level", style: EXCEL_HEADER_CELL_STYLE, width: { wch: "35" } },
    { title: "Status", style: EXCEL_HEADER_CELL_STYLE, width: { wch: "25" } },
    { title: "Topic", style: EXCEL_HEADER_CELL_STYLE, width: { wch: "25" } },
    { title: "Created date", style: EXCEL_HEADER_CELL_STYLE, width: { wch: "40" } }
  ]

  function toggleQuizStatus(quiz) {
    const successHandler = res => {
      successNoti("Thay đổi trạng thái quiz thành công, xem kết quả trên giao diện!", true);
      quiz.statusId = quiz.statusId === 'STATUS_PUBLIC' ? 'STATUS_PRIVATE' : 'STATUS_PUBLIC';
      setQuizsOfCourse([...quizsOfCourse]);
    }
    const errorHandlers = {
      onError: () => errorNoti("Đã xảy ra lỗi khi thay đổi trạng thái", true)
    }
    request("GET", `/change-quiz-open-close-status/${quiz.questionId}`, successHandler, errorHandlers);
  }

  const excelFileName = `Danh sách câu hỏi môn ${courseId}`;
  const excelExportedQuizs = [{
    columns: excelColumns,
    data: quizsOfCourse.map(mapQuizToExcelDataRow),
  }]

  const ExcelExportButton = (
    <ExcelExporter filename={excelFileName}
                   sheets={[{
                     name: excelFileName,
                     dataSet: excelExportedQuizs
                   }]}
    />
  )

  const PdfExportButton = (
    <Button color="primary" variant="contained"
            onClick={() => exportQuizsListPdf(quizsOfCourse, courseId)}>
      Xuất PDF
    </Button>
  )

  const CreateQuizButton = (
    <Button color="primary" variant="contained"
            onClick={() => history.push(`quiz/create/${courseId}`)}>
      <AddIcon/> Thêm mới
    </Button>
  )

  const ToggleQuizStatusButton = ({ quiz }) => (
    <Button color="primary" variant="outlined"
            onClick={() => toggleQuizStatus(quiz)}>
      Update status
    </Button>
  )

  const tableColumns = [
    { title: "Question ID", field: "questionId",
      render: (question) => (
        <Link to={`/edu/teacher/course/quiz/detail/${question.questionId}/${courseId}`}>
          {question.questionId}
        </Link>
      ),
    },
    { title: "Mức độ", field: "levelId", lookup: QUIZ_LEVELS, cellStyle: { maxWidth: '100px'} },
    { title: "Trạng thái", field: "statusId", lookup: QUIZ_STATUSES, cellStyle: { maxWidth: '160px'} },
    { title: "Chủ đề", field: "quizCourseTopic.quizCourseTopicId" },
    { title: "Người tạo", field: "createdByUserLoginId" },
    { title: "Ngày tạo", field: "createdStamp", cellStyle: { maxWidth: '108px'} },
    { field: "", title: "", cellStyle: { maxWidth: '140px', padding: '5px', textAlign: "center" },
      render: (question) => <ToggleQuizStatusButton quiz={question}/>
    }
  ]

  const actions = [
    { icon: () => ExcelExportButton, isFreeAction: true },
    { icon: () => PdfExportButton, isFreeAction: true },
    { icon: () => CreateQuizButton, isFreeAction: true }
  ]

  return (
    <Card>
      <CardContent className={classes.tableWrapper}>
        <StandardTable  title="Danh sách quiz"
                        columns={tableColumns}
                        data={quizsOfCourse}
                        hideCommandBar
                        options={{
                          selection: false,
                          search: true,
                          sorting: true
                        }}
                        actions={actions}/>
      </CardContent>
    </Card>
  );
}

function mapQuizToExcelDataRow(quiz) {
  return [
    { value: quiz.questionId, style: EXCEL_DATA_CELL_STYLE },
    { value: quiz.levelId, style: EXCEL_DATA_CELL_STYLE },
    { value: quiz.statusId ?? "", style: EXCEL_DATA_CELL_STYLE },
    { value: quiz.quizCourseTopic.quizCourseTopicId, style: EXCEL_DATA_CELL_STYLE },
    { value: quiz.createdStamp ?? "", style: EXCEL_DATA_CELL_STYLE }
  ];
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

const QUIZ_LEVELS = {
  QUIZ_LEVEL_EASY: 'Dễ',
  QUIZ_LEVEL_IMMEDIATE: 'Trung bình',
  QUIZ_LEVEL_HARD: 'Khó'
}

const QUIZ_STATUSES = {
  STATUS_PUBLIC: 'Công khai',
  STATUS_PRIVATE: 'Không công khai',
  STATUS_DELETE: 'Đã xóa'
}

TeacherViewQuizListOfCourse.propTypes = {
  courseId: PropTypes.string.isRequired
}
