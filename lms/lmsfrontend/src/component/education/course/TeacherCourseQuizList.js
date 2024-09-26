import {Card, CardContent} from "@material-ui/core/";
import {makeStyles} from "@material-ui/core/styles";
import AddIcon from "@material-ui/icons/Add";
import MaterialTable from "material-table";
import {useEffect, useState} from "react";
import ReactExport from "react-data-export";
import {Link, useHistory} from "react-router-dom";
import {request} from "../../../api";
import PositiveButton from "../classmanagement/PositiveButton";
import {exportQuizsListPdf} from "./TeacherCourseQuizListExportPDF.js";

const ExcelFile = ReactExport.ExcelFile;
const ExcelSheet = ReactExport.ExcelFile.ExcelSheet;

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
    borderRadius: "6px",
  },
  registrationBtn: {},
}));

function TeacherCourseQuizList(props) {
  const classes = useStyles();
  const courseId = props.courseId;

  const history = useHistory();
  const [quizs, setQuizs] = useState([]);
  const [fetchedQuizs, setfetchedQuizs] = useState(false);
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

  const DataSet = [
    {
      columns: [
        {
          title: "QuestionId",
          ...TableHeaderStyle,
          width: { wch: "50" },
        },
        {
          title: "Level",
          ...TableHeaderStyle,
          width: { wch: "35" },
        },
        {
          title: "Status",
          ...TableHeaderStyle,
          width: { wch: "25" },
        },
        {
          title: "Topic",
          ...TableHeaderStyle,
          width: { wch: "25" },
        },
        {
          title: "Created date",
          ...TableHeaderStyle,
          width: { wch: "40" },
        },
      ],
      data: !fetchedQuizs
        ? []
        : quizs.map((quiz) => {
            return [
              {
                value: quiz.questionId,
                ...TableCellStyle,
              },
              {
                value: quiz.levelId,
                ...TableCellStyle,
              },
              {
                value: quiz.statusId ? quiz.statusId : "",
                ...TableCellStyle,
              },
              {
                value: quiz.quizCourseTopic.quizCourseTopicId,
                ...TableCellStyle,
              },
              {
                value: quiz.createdStamp ? quiz.createdStamp : "",
                ...TableCellStyle,
              },
            ];
          }),
    },
  ];
  const columns = [
    {
      title: "QuestionId",
      field: "questionId",
      render: (rowData) => (
        <Link
          to={
            "/edu/teacher/course/quiz/detail/" +
            rowData["questionId"] +
            "/" +
            courseId
          }
        >
          {rowData["questionId"]}
        </Link>
      ),
    },
    { title: "Level", field: "levelId" },
    { title: "Status", field: "statusId" },
    { title: "Topic", field: "quizCourseTopic.quizCourseTopicId" },
    { title: "Created By", field: "createdByUserLoginId" },
    { title: "Created date", field: "createdStamp" },
    {
      field: "",
      title: "",
      cellStyle: { textAlign: "center" },
      render: (rowData) => (
        <PositiveButton
          label="Thay đổi trạng thái"
          disableRipple
          className={classes.registrationBtn}
          onClick={() => changeStatus(rowData)}
        />
      ),
    },
  ];

  const changeStatus = (rowData) => {
    //alert('change status');

    request(
      "get",
      "/change-quiz-open-close-status/" + rowData.questionId,
      (res) => {
        const quiz = res.data;
        console.log("change status, return status = " + quiz);
        history.push("/edu/course/detail/" + courseId);
      }
    );
  };

  async function getQuestionList() {
    //let lst = await authGet(dispatch, token, '/get-all-quiz-questions');
    request(
      "get",
      //"/get-quiz-of-course/" + courseId).then(
      "/get-quiz-of-course-sorted-created-time-desc/" + courseId,
      (res) => {
        res = res.data;
        setfetchedQuizs(true);
        if (res) {
          setQuizs(res);
        }
      }
    );
  }

  useEffect(() => {
    getQuestionList();
  }, []);

  return (
    <Card>
      <CardContent>
        <MaterialTable
          title={"Quizs"}
          columns={columns}
          data={quizs}
          actions={[
            quizs.length === 0
              ? null
              : {
                  icon: () => {
                    return (
                      <ExcelFile
                        filename={"Danh sách câu hỏi môn " + courseId}
                        element={
                          <img
                            alt="Xuất Excel"
                            src="/static/images/icons/excel_icon.png"
                            style={{ width: "35px", height: "35px" }}
                          ></img>
                        }
                      >
                        <ExcelSheet
                          dataSet={DataSet}
                          name={"Danh sách câu hỏi môn " + courseId}
                        />
                      </ExcelFile>
                    );
                  },
                  tooltip: "Xuất Excel",
                  isFreeAction: true,
                },
            quizs.length === 0
              ? null
              : {
                  icon: () => {
                    return (
                      <img
                        alt="Xuất PDF"
                        src="/static/images/icons/pdf_icon.png"
                        style={{ width: "35px", height: "35px" }}
                      ></img>
                    );
                  },
                  tooltip: "Xuất PDF",
                  isFreeAction: true,
                  onClick: () => {
                    exportQuizsListPdf(quizs, courseId);
                  },
                },
            {
              icon: () => {
                return <AddIcon color="primary" fontSize="large" />;
              },
              tooltip: "Thêm mới",
              isFreeAction: true,
              onClick: () => {
                history.push("quiz/create/" + courseId);
              },
            },
          ]}
        />
      </CardContent>
    </Card>
  );
}

export default TeacherCourseQuizList;
